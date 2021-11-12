package discord.commands.music;

import discord.audio.*;
import discord.audio.lavaplayer.AudioPlayerSendHandler;
import discord.audio.lavaplayer.AudioResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord.snippets.Embeds.errors.NotFoundError;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Embeds.trackInfo.PlayTrack;
import discord.snippets.Messages;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import utils.Checks;
import utils.customExceptions.ChannelNotFoundException;
import utils.customExceptions.GuildNotFoundException;
import utils.customExceptions.JDANotFoundException;
import utils.customExceptions.LinkInterpretation.*;
import utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFoundException;
import utils.customExceptions.checks.CheckFailedException;
import utils.linkProcessing.LinkInterpreter;
import utils.linkProcessing.interpretations.Interpretation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Play {

    private static void sendEphermal(
            @NotNull Interaction event,
            MessageEmbed messageEmbed) {
        event
                .replyEmbeds(messageEmbed)
                .setEphemeral(true)
                .queue();
    }

    public static void trigger(@NotNull SlashCommandEvent event, String url) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        try {
            Checks.slashCommandCheck(
                    event,
                    applicationId,
                    member,
                    guild
            );
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
            sendEphermal(
                    event,
                    StandardError.build("Could not get an AudioManager")
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

        if (!LinkInterpreter.isValid(url)) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.INVALID_URL)
            );
            return;
        }

        String soundCloudSearcher;
        try {
            soundCloudSearcher = LinkInterpreter.convertToSoundCloud(url);
        } catch(PlatformNotFoundException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.PLATFORM_NOT_FOUND)
            );
            return;
        } catch(InvalidURLException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.INVALID_URL)
            );
            return;
        } catch(VideoNotFoundException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.VIDEO_NOT_FOUND)
            );
            return;
        } catch(InternalError e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.INTERNAL_ERROR)
            );
            return;
        } catch(IOException | RequestException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.COULD_NOT_SEND_REQUEST)
            );
            return;
        } catch(IllegalStateException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.GENERAL_ERROR)
            );
            return;
        } catch (InvalidPlatformException e) {
            sendEphermal(
                    event,
                    StandardError.build(Messages.INVALID_PLATFORM)
            );
            return;
        }

        InteractionHook interactionHook = event.getHook();
        event.deferReply().queue();

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
                event
                        .replyEmbeds(
                                StandardError.build(Messages.INVALID_URL)
                        )
                        .queue();
                return;
            } catch(PlatformNotFoundException e) {
                event
                        .replyEmbeds(
                                StandardError.build(Messages.PLATFORM_NOT_FOUND)
                        )
                        .queue();
                return;
            }

            try {
                event
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
                event
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
