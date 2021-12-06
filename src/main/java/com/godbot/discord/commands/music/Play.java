package com.godbot.discord.commands.music;

import com.godbot.discord.audio.AudioManagerVault;
import com.godbot.discord.audio.PlayerManager;
import com.godbot.discord.audio.PlayerVault;
import com.godbot.discord.audio.lavaplayer.AudioPlayerSendHandler;
import com.godbot.discord.audio.lavaplayer.AudioResultHandler;
import com.godbot.discord.commands.Command;
import com.godbot.discord.snippets.Embeds.errors.EmptyError;
import com.godbot.discord.snippets.Embeds.errors.NotFoundError;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.godbot.discord.snippets.Embeds.trackInfo.PlayPlaylist;
import com.godbot.discord.snippets.Embeds.trackInfo.PlayTrack;
import com.godbot.discord.snippets.Messages;
import com.godbot.utils.Checks;
import com.godbot.utils.customExceptions.ChannelNotFoundException;
import com.godbot.utils.customExceptions.GuildNotFoundException;
import com.godbot.utils.customExceptions.JDANotFoundException;
import com.godbot.utils.customExceptions.LinkInterpretation.InterpretationsEmpty;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.checks.CheckFailedException;
import com.godbot.utils.discord.EventExtender;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.InterpretationExtraction;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;
import com.godbot.utils.linkProcessing.LinkHelper;
import com.godbot.utils.linkProcessing.LinkInterpreter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Play implements Command {

    private static String checkParameters(EventExtender event)
            throws CheckFailedException {
        if (event.event.getOption("url") == null) {
            throw new CheckFailedException("No URL provided");
        }
        return Objects.requireNonNull(event.event.getOption("url")).getAsString();
    }

    public static void trigger(@NotNull SlashCommandEvent scEvent) {
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

        AudioManager manager;
        try {
            manager = AudioManagerVault
                    .getInstance()
                    .getAudioManager(
                            applicationId,
                            guild.getId()
                    );
        } catch (JDANotFoundException e) {
            event
                    .replyEphemeral(
                            StandardError.build(Messages.GENERAL_ERROR)
                    );
            return;
        }
        AudioPlayer player;
        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            guild.getId(),
                            member.getVoiceState().getChannel().getId()
                    );
        } catch (GuildNotFoundException | ChannelNotFoundException e) {
            player = PlayerManager
                    .getInstance()
                    .createPlayer(
                            guild.getId(),
                            member.getVoiceState().getChannel().getId()
                    );
        }

        AudioSendHandler handler = manager.getSendingHandler();
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
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

        String type;
        try {
            if (LinkHelper.isVideo(url)) {
                type = "video";
            } else {
                type = "playlist";
            }
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

        AudioResultHandler audioResultHandler = new AudioResultHandler(
                player,
                manager,
                member.getVoiceState().getChannel(),
                url
        );

        if (type.equals("video")) {
            PlayerManager
                .getInstance()
                .getManager()
                .loadItem(
                        String.format("%s", url),
                        audioResultHandler
                );
        }

        switch (audioResultHandler.actionType) {
            case "error" -> {
                interactionHook
                        .editOriginalEmbeds(
                                StandardError.build(Messages.GENERAL_ERROR)
                        ).queue();
                return;
            }
            case "noMatches" -> {
                interactionHook
                        .editOriginalEmbeds(
                                NotFoundError.build(Messages.VIDEO_NOT_FOUND)
                        ).queue();
                return;
            }
            case "loadFailed" -> {
                interactionHook
                        .editOriginalEmbeds(
                                StandardError.build(Messages.LOADING_FAILED)
                        ).queue();
                return;
            }
        }

        HashMap<String, Interpretation> interpretationHashMap;
        try {
            interpretationHashMap = LinkInterpreter.interpret(url);
        } catch(InvalidURLException e) {
            interactionHook
                    .editOriginalEmbeds(
                            StandardError.build(Messages.INVALID_URL)
                    ).queue();
            return;
        } catch(PlatformNotFoundException e) {
            interactionHook
                    .editOriginalEmbeds(
                            StandardError.build(Messages.PLATFORM_NOT_FOUND)
                    ).queue();
            return;
        }

        if (interpretationHashMap.isEmpty()) {
            interactionHook
                    .editOriginalEmbeds(
                            EmptyError.build(Messages.INTERPRETATIONS_EMPTY)
                    ).queue();
        }

        try {
            if (type.equals("video")) {
                interactionHook
                        .editOriginalEmbeds(
                                PlayTrack.build(
                                        audioResultHandler.audioTrack,
                                        member,
                                        audioResultHandler.nowPlaying,
                                        interpretationHashMap
                                )
                        ).queue();
            } else {
                YoutubePlaylistInterpretation youtubePlaylistInterpretation =
                        InterpretationExtraction.getYTPlaylistInterpretation(interpretationHashMap);
                if (youtubePlaylistInterpretation == null) {
                    interactionHook
                            .editOriginalEmbeds(
                                    EmptyError.build(
                                            Messages.INTERPRETATIONS_EMPTY
                                    )
                            ).queue();
                    return;
                }
                interactionHook
                        .editOriginalEmbeds(
                                PlayPlaylist.build(
                                        member,
                                        audioResultHandler.nowPlaying,
                                        youtubePlaylistInterpretation
                                )
                        ).queue();
            }
        } catch (InterpretationsEmpty e) {
            interactionHook
                    .editOriginalEmbeds(
                            EmptyError.build(Messages.INTERPRETATIONS_EMPTY)
                    ).queue();
        }
    }
}
