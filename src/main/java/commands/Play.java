package commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import io.github.cdimascio.dotenv.Dotenv;
import lavaplayerHandlers.AudioResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import playableInfo.PlayableInfo;
import playableInfo.YouTubePlaylist;
import singeltons.AudioPlayerManagerWrapper;
import singeltons.JDAManager;
import snippets.ErrorMessages;
import snippets.ErrorsKt;
import snippets.InterpretationKeys;
import snippets.TrackInfoKt;
import utils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Play implements Command {
    // TODO Emphasize EventExtender.interpretations store interpretations in there

    public static void playPlaylist(
            AudioPlayerExtender audioPlayer,
            List<String> videoIds,
            Member member
    ) {
        AudioPlayerManager audioPlayerManager = AudioPlayerManagerWrapper
                .getInstance()
                .getManager();

        for (String videoId : videoIds) {
            AudioResultHandler audioResultHandler = new AudioResultHandler(
                    audioPlayer,
                    member
            );

            audioPlayerManager.loadItem(
                    String.format(
                            "https://www.youtube.com/watch?v=%s",
                            videoId
                    ),
                    audioResultHandler
            );

            while (audioResultHandler.actionType == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException ignore) {}
            }
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

    public static Future<HashMap<String, PlayableInfo>> startInterpretation(String url) {
        return Executors.newCachedThreadPool().submit(() -> LinkInterpreter.interpret(url));
    }

    public static AudioResultHandler playVideo(
            AudioPlayerExtender audioPlayer,
            String url,
            Member member
    ) {
        AudioResultHandler audioResultHandler = new AudioResultHandler(
                audioPlayer,
                member
        );

        AudioPlayerManagerWrapper
                .getInstance()
                .getManager()
                .loadItem(
                        url,
                        audioResultHandler
                );

        return audioResultHandler;
    }

    private static void processPlaylist() {
        // TODO
    }

    private static void processVideo() {
        // TODO
    }

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        EventExtender event = new EventExtender(scEvent);
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

        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        VoiceChannel voiceChannel;

        try {
            voiceChannel = Checks.slashCommandCheck(
                    applicationId,
                    member,
                    guild
            );
        } catch (VoiceCheckFailedException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NOT_CONNECTED_TO_VC
                    )
                    );
            return;
        } catch (CheckFailedException e) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.GENERAL_ERROR
                    )
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
                        guild.getId(),
                        voiceChannel
                );

        if (!player.getVoiceChannel().getId().equals(voiceChannel.getId())) {
            event.replyEphemeral(
                    ErrorsKt.standardError(
                            ErrorMessages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        InteractionHook interactionHook = scEvent.getHook();
        scEvent.deferReply().queue();

        Future<HashMap<String, PlayableInfo>> interpretationFuture = startInterpretation(url);

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
                    ).queue();
            return;
        }

        if (!isVideo) {
            String firstUrl = null;
            try {
                firstUrl = LinkInterpreter.getFirst(url);
            } catch (IOException | RequestException ignore) {}
            catch (InvalidURLException e) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(ErrorMessages.INVALID_URL)
                        ).queue();
                return;
            } catch (PlatformNotFoundException e) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(ErrorMessages.PLATFORM_NOT_FOUND)
                        ).queue();
                return;
            }

            if (player.getAudioPlayer().getPlayingTrack() == null) {
                if (firstUrl != null) {
                    Executors.newCachedThreadPool().submit(() -> playVideo(
                            player,
                            url,
                            member
                    ));
                }
            }

            HashMap<String, PlayableInfo> songInformation;
            try{
                songInformation =
                        interpretationFuture.get();
            } catch (InterruptedException e) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(
                                        ErrorMessages.INFO_GATHERING_SONG_FAILED
                                )
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
                                    ErrorsKt.standardError(
                                            ErrorMessages.PLATFORM_NOT_FOUND
                                    )
                            ).queue();
                    return;
                }
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(
                                        ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED
                                )
                        ).queue();
                return;
            }

            YouTubePlaylist youTubePlaylist;

            if (!songInformation.containsKey(InterpretationKeys.YTPLAYLIST)) {
                interactionHook
                        .sendMessageEmbeds(
                                ErrorsKt.standardError(
                                        ErrorMessages.INFO_GATHERING_PLAYLIST_FAILED
                                )
                        ).queue();
                return;
            } else {
                youTubePlaylist = (YouTubePlaylist) songInformation.get(InterpretationKeys.YTPLAYLIST);
            }

            int positionInQueue = player.getQueue().size() + 1;
            AudioPlayerExtender audioPlayer = AudioPlayerManagerWrapper
                    .getInstance()
                    .getPlayer(
                            bot,
                            guild.getId(),
                            voiceChannel
                    );

            Executors.newCachedThreadPool().submit(() -> playPlaylist(
                    audioPlayer,
                    youTubePlaylist.getVideoIds(),
                    member
            ));

            interactionHook
                    .sendMessageEmbeds(
                            TrackInfoKt.playPlaylist(
                                    member,
                                    youTubePlaylist,
                                    positionInQueue,
                                    positionInQueue
                            )
                    ).queue();
        } else {
            AudioResultHandler audioResultHandler = playVideo(
                    player,
                    url,
                    member
            );

            while (audioResultHandler.actionType == 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException ignore) {}
            }

            switch (audioResultHandler.actionType) {
                case 10 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    ErrorsKt.standardError(ErrorMessages.GENERAL_ERROR)
                            ).queue();
                    return;
                }
                case 3 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    ErrorsKt.standardError(ErrorMessages.VIDEO_NOT_FOUND)
                            ).queue();
                    return;
                }
                case 4 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    ErrorsKt.standardError(ErrorMessages.LOADING_FAILED)
                            ).queue();
                    return;
                }
            }

            HashMap<String, PlayableInfo> playableInfo;
            try {
                playableInfo = interpretationFuture.get();
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
                                    ErrorsKt.standardError(
                                            ErrorMessages.PLATFORM_NOT_FOUND
                                    )
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

            interactionHook.sendMessageEmbeds(
                    TrackInfoKt.playVideo(
                    member,
                    audioResultHandler.position == 0,
                    playableInfo,
                    player.getQueue().size() + 1,
                    player.getQueue().size() + 1
            )).queue();
        }
    }
}
