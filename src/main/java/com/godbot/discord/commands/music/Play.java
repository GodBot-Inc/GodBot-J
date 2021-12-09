package com.godbot.discord.commands.music;

import com.godbot.discord.JDAManager;
import com.godbot.discord.audio.AudioManagerVault;
import com.godbot.discord.audio.AudioPlayerManagerWrapper;
import com.godbot.discord.audio.lavaplayer.AudioResultHandler;
import com.godbot.discord.commands.Command;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.discord.snippets.Embeds.trackInfo.PlayTrack;
import com.godbot.discord.snippets.Messages;
import com.godbot.utils.Checks;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.checks.CheckFailedException;
import com.godbot.utils.customExceptions.requests.RequestException;
import com.godbot.utils.discord.EventExtender;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.linkProcessing.LinkHelper;
import com.godbot.utils.linkProcessing.LinkInterpreter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Play implements Command {

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
                        godbotJDA, guildId
                );
        audioManagerVault.checkSendingHandler(
                godbotJDA,
                guildId,
                player
        );

        AudioResultHandler audioResultHandler = new AudioResultHandler(
                player,
                audioManager,
                voiceChannel,
                url
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

        Future<HashMap<String, Interpretation>> future =
                Executors.newCachedThreadPool()
                        .submit(() -> LinkInterpreter.interpret(url));

        boolean isVideo;
        try {
            isVideo = LinkHelper.isVideo(url);
        } catch (InvalidURLException e) {
            interactionHook
                    .editOriginalEmbeds(
                            StandardError.build(Messages.PLAY_INVALID_URL)
                    ).queue();
            return;
        } catch (PlatformNotFoundException e) {
            interactionHook
                    .editOriginalEmbeds(
                            StandardError.build(Messages.PLATFORM_NOT_FOUND)
                    ).queue();
            return;
        }

        Future<HashMap<String, Interpretation>> interpretations = startInterpretation(url);
        if (!isVideo) {
            String firstUrl;
            try {
                firstUrl = LinkInterpreter.getFirst(url);
            } catch (IOException | RequestException ignore) {}
            catch (InvalidURLException e) {
                interactionHook
                        .editOriginalEmbeds(
                                StandardError.build(Messages.INVALID_URL)
                        ).queue();
                return;
            } catch (PlatformNotFoundException e) {
                interactionHook
                        .editOriginalEmbeds(
                                StandardError.build(Messages.PLATFORM_NOT_FOUND)
                        ).queue();
                return;
            }
            interactionHook
                    .editOriginalEmbeds(
                            StandardError.build(
                                    "Playlists are not supported yet"
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

            System.out.println("response received");

            switch (audioResultHandler.getActionType()) {
                case 10 -> {
                    interactionHook
                            .editOriginalEmbeds(
                                    StandardError.build(Messages.GENERAL_ERROR)
                            ).queue();
                    return;
                }
                case 3 -> {
                    interactionHook
                            .editOriginalEmbeds(
                                    StandardError.build(Messages.VIDEO_NOT_FOUND)
                            ).queue();
                    return;
                }
                case 4 -> {
                    interactionHook
                            .editOriginalEmbeds(
                                    StandardError.build(Messages.LOADING_FAILED)
                            ).queue();
                    return;
                }
            }

            HashMap<String, Interpretation> interpretationHashMap;
            try {
                interpretationHashMap = interpretations.get();
            } catch (InterruptedException e) {
                System.out.println("interrupted");
                try {
                    interpretationHashMap = LinkInterpreter.interpret(url);
                } catch (Exception e2) {
                    System.out.println("sync failed");
                    interactionHook
                            .editOriginalEmbeds(
                                    StandardError.build(
                                            Messages.INFO_GATHERING_SONG_FAILED
                                    )
                            ).queue();
                    return;
                }
            } catch (ExecutionException e) {
                System.out.println("execution exception");
                interactionHook
                        .editOriginalEmbeds(
                                StandardError.build(
                                        Messages.INFO_GATHERING_SONG_FAILED
                                )
                        ).queue();
                return;
            }

            System.out.println("building");

            interactionHook.sendMessageEmbeds(PlayTrack.build(
                    member,
                    audioResultHandler.isNowPlaying(),
                    interpretationHashMap
            )).queue();
        }
    }
}
