package com.godbot.discord.commands.music;

import com.godbot.discord.JDAManager;
import com.godbot.discord.audio.AudioManagerVault;
import com.godbot.discord.audio.AudioPlayerManagerWrapper;
import com.godbot.discord.audio.QueueSystem;
import com.godbot.discord.audio.lavaplayer.AudioResultHandler;
import com.godbot.discord.commands.Command;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.discord.snippets.Embeds.trackInfo.PlayPlaylist;
import com.godbot.discord.snippets.Embeds.trackInfo.PlayTrack;
import com.godbot.discord.snippets.Messages;
import com.godbot.utils.Checks;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.checks.CheckFailedException;
import com.godbot.utils.customExceptions.requests.RequestException;
import com.godbot.utils.discord.EventExtender;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.InterpretationExtraction;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;
import com.godbot.utils.linkProcessing.LinkHelper;
import com.godbot.utils.linkProcessing.LinkInterpreter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Play implements Command {

    public static void playPlaylist(
            String applicationId,
            String guildId,
            VoiceChannel voiceChannel,
            List<String> videoIds
    ) {
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);
        AudioPlayer player = AudioPlayerManagerWrapper.getInstance().getPlayer(guildId, voiceChannel.getId());
        AudioManagerVault audioManagerVault = AudioManagerVault.getInstance();
        AudioManager audioManager = audioManagerVault
                .getAudioManager(
                        godbotJDA,
                        guildId
                );
        audioManagerVault.checkSendingHandler(
                godbotJDA,
                guildId,
                player
        );

        AudioPlayerManager audioPlayerManager = AudioPlayerManagerWrapper
                .getInstance()
                .getManager();

        for (String videoId : videoIds) {
            audioPlayerManager.loadItem(
                    String.format(
                            "https://www.youtube.com/watch?v=%s",
                            videoId
                    ),
                    new AudioResultHandler(
                            player,
                            audioManager,
                            voiceChannel
                    )
            );
        }
    }

    private static String checkParameters(EventExtender event)
            throws CheckFailedException {
        OptionMapping url = event.event.getOption("url");
        if (url == null) {
            throw new CheckFailedException("No URL provided");
        }

        return url.getAsString();
    }

    public static void startConvertion(String url) {

    }

    public static Future<HashMap<String, Interpretation>> startInterpretation(String url) {
        return Executors.newCachedThreadPool().submit(() -> LinkInterpreter.interpret(url));
    }

    public static AudioResultHandler playVideo(
            String applicationId,
            String guildId,
            VoiceChannel voiceChannel,
            String url
    ) {
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);
        AudioPlayer player = AudioPlayerManagerWrapper.getInstance().getPlayer(guildId, voiceChannel.getId());
        AudioManagerVault audioManagerVault = AudioManagerVault.getInstance();
        AudioManager audioManager = audioManagerVault
                .getAudioManager(
                        godbotJDA,
                        guildId
                );
        audioManagerVault.checkSendingHandler(
                godbotJDA,
                guildId,
                player
        );

        AudioResultHandler audioResultHandler = new AudioResultHandler(
                player,
                audioManager,
                voiceChannel
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

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
        System.out.println("tp1");
        EventExtender event = new EventExtender(scEvent);
        String url;
        try {
            url = checkParameters(event);
        } catch (CheckFailedException e) {
            event
                .replyEphemeral(
                        StandardError.build("No URL provided")
                );
            return;
        }

        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        try {
            Checks.slashCommandCheck(
                    scEvent,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
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
                            StandardError.build(Messages.PLAY_INVALID_URL)
                    );
            return;
        }

        InteractionHook interactionHook = scEvent.getHook();
        scEvent.deferReply().queue();

        Future<HashMap<String, Interpretation>> interpretationFuture = startInterpretation(url);

        boolean isVideo;
        try {
            isVideo = LinkHelper.isVideo(url);
        } catch (InvalidURLException e) {
            interactionHook
                    .sendMessageEmbeds(
                            StandardError.build(Messages.PLAY_INVALID_URL)
                    ).queue();
            return;
        } catch (PlatformNotFoundException e) {
            interactionHook
                    .sendMessageEmbeds(
                            StandardError.build(Messages.PLATFORM_NOT_FOUND)
                    ).queue();
            return;
        }

        //TODO: Start convertion here
        if (!isVideo) {
            String firstUrl = null;
            try {
                firstUrl = LinkInterpreter.getFirst(url);
            } catch (IOException | RequestException ignore) {}
            catch (InvalidURLException e) {
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(Messages.INVALID_URL)
                        ).queue();
                return;
            } catch (PlatformNotFoundException e) {
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(Messages.PLATFORM_NOT_FOUND)
                        ).queue();
                return;
            }

            AudioPlayer player = AudioPlayerManagerWrapper
                    .getInstance()
                    .getPlayer(
                            guild.getId(),
                            member.getVoiceState().getChannel().getId()
                    );

            boolean nowPlaying = false;
            if (player.getPlayingTrack() == null) {
                nowPlaying = true;
                if (firstUrl != null) {
                    String finalFirstUrl = firstUrl;
                    Executors.newCachedThreadPool().submit(() -> playVideo(
                            applicationId,
                            guild.getId(),
                            member.getVoiceState().getChannel(),
                            finalFirstUrl
                    ));
                }
            }

            HashMap<String, Interpretation> interpretationHashMap;
            try{
                interpretationHashMap =
                        interpretationFuture.get();
            } catch (InterruptedException e) {
                try {
                    interpretationHashMap = LinkInterpreter.interpret(url);
                } catch (Exception e2) {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(
                                            Messages.INFO_GATHERING_PLAYLIST_FAILED
                                    )
                            ).queue();
                    return;
                }
            } catch (ExecutionException e) {
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(
                                        Messages.INFO_GATHERING_PLAYLIST_FAILED
                                )
                        ).queue();
                return;
            }

            YoutubePlaylistInterpretation ytPlaylistInterpretation =
                    InterpretationExtraction.getYTPlaylistInterpretation(interpretationHashMap);

            if (ytPlaylistInterpretation == null) {
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(
                                        Messages.INTERPRETATIONS_FAILED
                                )
                        ).queue();
                return;
            }

            if (ytPlaylistInterpretation.getVideoIds() == null) {
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(
                                        Messages.INTERPRETATIONS_EMPTY
                                )
                        ).queue();
                return;
            }

            Executors.newCachedThreadPool().submit(() -> playPlaylist(
                    applicationId,
                    guild.getId(),
                    member.getVoiceState().getChannel(),
                    ytPlaylistInterpretation.getVideoIds()
            ));

            interactionHook
                    .sendMessageEmbeds(
                            PlayPlaylist.standard(
                                    member,
                                    nowPlaying,
                                    ytPlaylistInterpretation
                            )
                    ).queue();
        } else {
            AudioResultHandler audioResultHandler = playVideo(
                    applicationId,
                    guild.getId(),
                    member.getVoiceState().getChannel(),
                    url
            );

            while (audioResultHandler.getActionType() == 0) {
                System.out.println("checking response");
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException ignore) {}
            }

            switch (audioResultHandler.getActionType()) {
                case 10 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(Messages.GENERAL_ERROR)
                            ).queue();
                    return;
                }
                case 3 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(Messages.VIDEO_NOT_FOUND)
                            ).queue();
                    return;
                }
                case 4 -> {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(Messages.LOADING_FAILED)
                            ).queue();
                    return;
                }
            }

            HashMap<String, Interpretation> interpretationHashMap;
            try {
                interpretationHashMap = interpretationFuture.get();
            } catch (InterruptedException e) {
                try {
                    interpretationHashMap = LinkInterpreter.interpret(url);
                } catch (InvalidURLException invalidURLException) {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(
                                            Messages.INVALID_URL
                                    )
                            ).queue();
                    return;
                } catch (PlatformNotFoundException platformNotFoundException) {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(
                                            Messages.PLATFORM_NOT_FOUND
                                    )
                            ).queue();
                    return;
                }
            } catch (ExecutionException e) {
                System.out.println(e.getCause().toString());
                if (e.getCause() instanceof InvalidURLException) {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(
                                             Messages.INVALID_URL
                                    )
                            ).queue();
                    return;
                } else if (e.getCause() instanceof PlatformNotFoundException) {
                    interactionHook
                            .sendMessageEmbeds(
                                    StandardError.build(
                                            Messages.PLATFORM_NOT_FOUND
                                    )
                            ).queue();
                    return;
                }
                interactionHook
                        .sendMessageEmbeds(
                                StandardError.build(
                                        Messages.INFO_GATHERING_SONG_FAILED
                                )
                        ).queue();
                return;
            }

            List<AudioTrack> queue = QueueSystem
                    .getInstance()
                    .getQueue(
                            AudioPlayerManagerWrapper
                                    .getInstance()
                                    .getPlayer(
                                            guild.getId(),
                                            member.getVoiceState().getChannel().getId()
                                    )
                    );

            int positionInQueue = queue.size() + 1;

            interactionHook.sendMessageEmbeds(
                    PlayTrack.standard(
                    member,
                    audioResultHandler.isNowPlaying(),
                    interpretationHashMap,
                    positionInQueue,
                    positionInQueue
            )).queue();
        }
    }
}
