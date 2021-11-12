package discord.commands.musicControl;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord.audio.PlayerVault;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Messages;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.Checks;
import utils.customExceptions.ChannelNotFoundException;
import utils.customExceptions.GuildNotFoundException;
import utils.customExceptions.checks.CheckFailedException;
import utils.discord.EventExtender;

public class Pause {
    public static void trigger(SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        EventExtender event = new EventExtender(scEvent);

        try {
            Checks.slashCommandCheck(
                    scEvent,
                    applicationId,
                    member,
                    guild
            );
        } catch (CheckFailedException e) {
            event.sendEphermal(
                    StandardError.build(
                            Messages.GENERAL_ERROR
                    )
            );
            return;
        }

        AudioPlayer player;

        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            guild.getId(),
                            member
                                    .getVoiceState()
                                    .getChannel()
                                    .getId()
                    );
        } catch (GuildNotFoundException e) {
            event.sendEphermal(
                    StandardError.build(
                            Messages.NO_PLAYER_IN_GUILD
                    )
            );
            return;
        } catch (ChannelNotFoundException e) {
            event.sendEphermal(
                    StandardError.build(
                            Messages.NO_PLAYER_IN_VC
                    )
            );
            return;
        }

        if (player.getPlayingTrack() == null) {
            event.sendEphermal(
                    StandardError.build(
                            Messages.NO_PLAYING_TRACK
                    )
            );
        }
        if (player.isPaused()) {
            event.sendEphermal(
                    StandardError.build(
                            "Player is already paused"
                    )
            );
        }

        player.setPaused(true);
        scEvent.replyEmbeds(
                new EmbedBuilder()
                        .setDescription(
                                String.format(
                                        ":pause_button: Paused track [%s - %s](%s)",
                                        player.getPlayingTrack().getInfo().title,
                                        player.getPlayingTrack().getInfo().author,
                                        player.getPlayingTrack().getInfo().uri
                                )
                        )
                        .build()
        )
                .queue();
    }
}
