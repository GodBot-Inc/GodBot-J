package commands;

import ktSnippets.ErrorsKt;
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
            boolean shuffle
    ) {
        audioPlayer.openConnection();
        if (shuffle) {
            Collections.shuffle(playlistInfo.getPlayableInformation());
        }
        for (PlayableInfo playableInfo : playlistInfo.getPlayableInformation()) {
            try {
                audioPlayer.play(new AudioTrackExtender(playableInfo, requester));
            } catch (GodBotException ignore) {}
        }
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
            Member member,
            boolean shuffle,
            InteractionHook interactionHook,
            Future<PlayableInfo> playableFuture
    ) {
        PlaylistPlayableInfo playlistInformation;
        try{
            playlistInformation = (PlaylistPlayableInfo) playableFuture.get();
        } catch (InterruptedException e) {
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(
                                    ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED
                            )
                    ).queue();
            return;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvalidURLException) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(
                                        ErrorMessages.PLAY_INVALID_URL
                                )
                        ).queue();
                return;
            } else if (e.getCause() instanceof PlatformNotFoundException) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(ErrorMessages.PLATFORM_NOT_FOUND)
                        ).queue();
                return;
            }
            e.printStackTrace();
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(
                                    ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED
                            )
                    ).queue();
            return;
        }

        int positionInQueue = player.getQueue().size() + 1;

        Executors.newCachedThreadPool().submit(() -> playPlaylist(
                player,
                playlistInformation,
                member,
                shuffle
        ));

        interactionHook
                .sendMessageEmbeds(
                        TrackInfoKt.playPlaylist(
                                member,
                                playlistInformation,
                                positionInQueue,
                                positionInQueue + playlistInformation.getVideoIds().size() - 1
                        )
                ).queue();
    }

    private static void processVideo(
            AudioPlayerExtender player,
            Member member,
            InteractionHook interactionHook,
            Future<PlayableInfo> playableFuture
    ) {
        PlayableInfo playableInfo;
        try {
            playableInfo = playableFuture.get();
        } catch (InterruptedException e) {
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(ErrorMessages.INFO_GATHERING_SONG_FAILED)
                    ).queue();
            return;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof InvalidURLException) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(
                                        ErrorMessages.INVALID_URL
                                )
                        ).queue();
                return;
            } else if (e.getCause() instanceof PlatformNotFoundException) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(ErrorMessages.PLATFORM_NOT_FOUND)
                        ).queue();
                return;
            }
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(
                                    ErrorMessages.INFO_GATHERING_SONG_FAILED
                            )
                    ).queue();
            return;
        }
        player.openConnection();

        int position;
        try {
            position = player.play(new AudioTrackExtender(playableInfo, member));
        } catch (GodBotException e) {
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.notFoundError(
                                    ErrorMessages.TRACK_NOT_FOUND
                            )
                    ).queue();
            return;
        }

        MessageEmbed embed = TrackInfoKt.playVideo(
                member,
                playableInfo,
                position,
                player.getQueue().size() + 1
        );
        interactionHook.sendMessageEmbeds(embed).queue();
    }

    public static void trigger(@NotNull EventExtender event, SlashCommandPayload payload) {
        String url;
        try {
            url = checkParameters(event);
        } catch (CheckFailedException e) {
            event
                .replyEphemeral(
                        ErrorsKt.standardError("No URL provided")
                );
            return;
        }

        try {
            checkParameters(event);
        } catch (CheckFailedException e) {
            return;
        }

        if (Checks.linkIsValid(url)) {
            event
                    .replyEphemeral(
                            ErrorsKt.standardError(ErrorMessages.PLAY_INVALID_URL)
                    );
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

        if (!player.getVoiceChannel().getId().equals(payload.getVoiceChannel().getId())) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        InteractionHook interactionHook = event.event.getHook();
        event.event.deferReply().queue();

        Future<PlayableInfo> infoGatheringFuture = startInfoGathering(url);

        boolean isVideo;
        try {
            isVideo = LinkHelper.isVideo(url);
        } catch (InvalidURLException e) {
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(ErrorMessages.PLAY_INVALID_URL)
                    ).queue();
            return;
        } catch (PlatformNotFoundException e) {
            interactionHook
                    .sendMessageEmbeds(
                            ErrorsKt.standardError(ErrorMessages.PLATFORM_NOT_FOUND)
                    )
                    .addActionRow()
                    .queue();
            return;
        }

        if (!isVideo) {
            processPlaylist(
                    player,
                    payload.getMember(),
                    getShuffle(event),
                    interactionHook,
                    infoGatheringFuture
            );
        } else {
            processVideo(
                    player,
                    payload.getMember(),
                    interactionHook,
                    infoGatheringFuture
            );
        }
    }
}
