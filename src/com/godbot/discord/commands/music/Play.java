package discord.commands.music;

import discord.audio.*;
import discord.audio.lavaplayer.AudioPlayerSendHandler;
import discord.audio.lavaplayer.AudioResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord.commands.Command;
import discord.snippets.Embeds.errors.NotFoundError;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Embeds.trackInfo.PlayTrack;
import discord.snippets.Messages;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import utils.Checks;
import utils.customExceptions.ChannelNotFoundException;
import utils.customExceptions.GuildNotFoundException;
import utils.customExceptions.JDANotFoundException;
import utils.customExceptions.LinkInterpretation.*;
import utils.customExceptions.checks.CheckFailedException;
import utils.discord.EventExtender;
import utils.linkProcessing.LinkInterpreter;
import utils.interpretations.Interpretation;

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

        AudioResultHandler audioResultHandler = new AudioResultHandler(
                player,
                interactionHook,
                manager,
                member.getVoiceState().getChannel(),
                url
        );
        PlayerManager
                .getInstance()
                .getManager()
                .loadItem(
                        String.format("%s", url),
                        audioResultHandler
                );

        if (Objects.equals(
                audioResultHandler.actionType,
                "trackLoaded"
        ) ||
                Objects.equals(
                        audioResultHandler.actionType,
                        "playlistLoaded"
                )
        ) {
            HashMap<String, Interpretation> interpretationHashMap;
            try {
                interpretationHashMap = LinkInterpreter.interpret(url);
            } catch(InvalidURLException e) {
                scEvent
                        .replyEmbeds(
                                StandardError.build(Messages.INVALID_URL)
                        )
                        .queue();
                return;
            } catch(PlatformNotFoundException e) {
                scEvent
                        .replyEmbeds(
                                StandardError.build(Messages.PLATFORM_NOT_FOUND)
                        )
                        .queue();
                return;
            }

            try {
                scEvent
                        .replyEmbeds(
                                PlayTrack.build(
                                        audioResultHandler.audioTrack,
                                        member,
                                        audioResultHandler.nowPlaying,
                                        interpretationHashMap
                                )
                        )
                        .queue();
            } catch (NoSCInterpretationException e) {
                scEvent
                        .replyEmbeds(
                                NotFoundError.build(
                                        "I could not find information about the song on SoundCloud"
                                )
                        )
                        .queue();
            }
        }
    }
}
