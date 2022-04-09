package commands;

import ktLogging.UtilsKt;
import ktLogging.custom.GodBotChildLogger;
import ktLogging.custom.GodBotLogger;
import ktSnippets.TrackInfoKt;
import ktUtils.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import playableInfo.PlayableInfo;
import playableInfo.PlaylistPlayableInfo;
import singeltons.AudioPlayerManagerWrapper;
import singeltons.JDAManager;
import snippets.ErrorMessages;
import utils.Checks;
import utils.DataGatherer;
import utils.EventExtender;
import utils.LinkHelper;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Play implements Command {
    // TODO Emphasize EventExtender.interpretations store interpretations in there

    public static void playPlaylist(
            AudioPlayerExtender audioPlayer,
            PlaylistPlayableInfo playlistInfo,
            Member requester,
            boolean shuffle,
            GodBotChildLogger logger
    ) {
        audioPlayer.openConnection();
        logger.info("Opened Audio Connection");
        if (shuffle) {
            Collections.shuffle(playlistInfo.getPlayableInformation());
        }
        for (PlayableInfo playableInfo : playlistInfo.getPlayableInformation()) {
            try {
                audioPlayer.play(new AudioTrackExtender(playableInfo, requester));
            } catch (GodBotException ignore) {}
        }
        logger.info("Adding of songs finished");
    }

    private static String checkParameters(EventExtender event)
            throws CheckFailedException {
        OptionMapping url = event.event.getOption("url");
        if (url == null) {
            throw new CheckFailedException();
        }
        return url.getAsString();
    }

    private static boolean getShuffle(EventExtender event) {
        OptionMapping shuffle = event.event.getOption("shuffle");
        return shuffle != null && shuffle.getAsBoolean();
    }

    public static Future<PlayableInfo> startInfoGathering(String url) {
        return Executors.newCachedThreadPool().submit(() -> DataGatherer.gatherPlayableInfo(url));
    }

    private static void processPlaylist(
            AudioPlayerExtender player,
            SlashCommandPayload payload,
            boolean shuffle,
            InteractionHook interactionHook,
            Future<PlayableInfo> playableFuture,
            GodBotChildLogger logger
    ) {
        PlaylistPlayableInfo playlistInformation;
        try{
            playlistInformation = (PlaylistPlayableInfo) playableFuture.get();
        } catch (InterruptedException e) {
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED, logger);
            return;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvalidURLException) {
                ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.PLAY_INVALID_URL, logger);
                return;
            } else if (e.getCause() instanceof PlatformNotFoundException) {
                ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.PLATFORM_NOT_FOUND, logger);
                return;
            }
            logger.error("Should not happen: " + e.getMessage());
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED);
            return;
        }

        int positionInQueue = player.getQueue().size() + 1;

        Executors.newCachedThreadPool().submit(() -> playPlaylist(
                player,
                playlistInformation,
                payload.getMember(),
                shuffle,
                logger
        ));
        logger.info("Started Play Playlist Async");

        interactionHook
                .sendMessageEmbeds(
                        TrackInfoKt.playPlaylist(
                                payload.getMember(),
                                playlistInformation,
                                positionInQueue,
                                positionInQueue + playlistInformation.getVideoIds().size() - 1
                        )
                ).queue();
    }

    private static void processVideo(
            AudioPlayerExtender player,
            SlashCommandPayload payload,
            InteractionHook interactionHook,
            Future<PlayableInfo> playableFuture,
            GodBotChildLogger logger
    ) {
        PlayableInfo playableInfo;
        try {
            playableInfo = playableFuture.get();
        } catch (InterruptedException e) {
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INFO_GATHERING_SONG_FAILED, logger);
            return;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvalidURLException) {
                ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INVALID_URL, logger);
                return;
            } else if (e.getCause() instanceof PlatformNotFoundException) {
                ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.PLATFORM_NOT_FOUND, logger);
                return;
            } else if (e.getCause() instanceof  IllegalStateException) {
                ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INVALID_PLATFORM, logger);
                return;
            }
            logger.error("Should not happen: " + e.getMessage());
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.INFO_GATHERING_SONG_FAILED);
            return;
        }
        player.openConnection();
        logger.info("Opened audio connection to " + player.getVoiceChannel().getName());

        int position;
        try {
            position = player.play(new AudioTrackExtender(playableInfo, payload.getMember()));
        } catch (GodBotException e) {
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.TRACK_NOT_FOUND, logger);
            return;
        }
        logger.info("Got Video Position: " + position);

        MessageEmbed embed = TrackInfoKt.playVideo(
                payload.getMember(),
                playableInfo,
                position,
                player.getQueue().size() + 1
        );
        interactionHook.sendMessageEmbeds(embed).queue();
        logger.info("Send Response");
    }

    public static void trigger(@NotNull EventExtender event, SlashCommandPayload payload) {
        GodBotChildLogger logger = new GodBotLogger().command(
                "Play",
                UtilsKt.formatPayload(payload)
        );
        String url;

        try {
            url = checkParameters(event);
        } catch (CheckFailedException e) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, "No URL provided");
            return;
        }
        logger.info("Got Url Parameter");

        if (Checks.linkIsValid(url)) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.PLAY_INVALID_URL);
            return;
        }

        JDA bot = JDAManager.getInstance().getJDA(applicationId);

        AudioPlayerExtender player = AudioPlayerManagerWrapper
                .getInstance()
                .getPlayer(
                        bot,
                        payload.getGuild().getId(),
                        payload.getVoiceChannel()
                );
        logger.info("Successfully got Player");

        if (!player.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            ErrorHandlerKt.handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger);
            return;
        }

        InteractionHook interactionHook = event.event.getHook();
        event.event.deferReply().queue();
        logger.info("Deferred Reply");

        Future<PlayableInfo> infoGatheringFuture = startInfoGathering(url);

        boolean isVideo;
        try {
            isVideo = LinkHelper.isVideo(url);
        } catch (InvalidURLException e) {
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.PLAY_INVALID_URL, logger);
            return;
        } catch (PlatformNotFoundException e) {
            ErrorHandlerKt.handleInteractionHookErrorResponse(interactionHook, payload, ErrorMessages.PLATFORM_NOT_FOUND, logger);
            return;
        }

        if (!isVideo) {
            processPlaylist(
                    player,
                    payload,
                    getShuffle(event),
                    interactionHook,
                    infoGatheringFuture,
                    logger
            );
        } else {
            // TODO: Add Logging
            processVideo(
                    player,
                    payload,
                    interactionHook,
                    infoGatheringFuture,
                    logger
            );
        }
    }
}
