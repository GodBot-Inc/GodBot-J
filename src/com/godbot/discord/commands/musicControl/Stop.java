package discord.commands.musicControl;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import discord.audio.PlayerManager;
import discord.audio.PlayerVault;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Messages;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.customExceptions.ChannelNotFoundException;
import utils.customExceptions.GuildNotFoundException;
import utils.discord.EventExtender;

public class Stop {

    public static void trigger(SlashCommandEvent scEvent) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = scEvent.getGuild();
        Member member = scEvent.getMember();
        String applicationId = dotenv.get("APPLICATIONID");

        EventExtender event = new EventExtender(scEvent);

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

        PlayerManager.getInstance().stopPlayer(player);

        scEvent
                .replyEmbeds(
                        new EmbedBuilder()
                                .setTitle(":stop_track: Player stopped")
                                .build()
                );
    }
}
