package discord.commands;

import discord.audio.*;
import discord.audio.lavaplayer.AudioPlayerSendHandler;
import discord.audio.lavaplayer.AudioResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import discord.JDAManager;
import utils.customExceptions.ChannelNotFound;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.audio.ApplicationNotFound;
import utils.presets.Embeds;

import java.security.KeyException;

public class Play {
    private static AudioManager getManager(String applicationId, Guild guild) throws KeyException {
        AudioManagerVault audioVault = AudioManagerVault.getInstance();
        try {
            return audioVault.getAudioManager(applicationId, guild.getId());
        } catch (GuildNotFound guildNotFound) {
            audioVault.registerGuild(applicationId, guild);
            return getManager(applicationId, guild);
        } catch (ApplicationNotFound applicationNotFound) {
            JDAManager jdaManager = JDAManager.getInstance();
            JDA godbotJDA = jdaManager.getJDA("godbot");
            audioVault.registerJDA(applicationId, godbotJDA.getGuilds());
            return getManager(applicationId, guild);
        }
    }

    public static void trigger(SlashCommandEvent event, String url) {
        Dotenv dotenv = Dotenv.load();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        String applicationId = dotenv.get("APPLICATIONID");
        if (applicationId == null) {
            event
                    .replyEmbeds(Embeds.error("Could not get the ApplicationId"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (guild == null) {
            event
                    .replyEmbeds(Embeds.error("Could not get the Guild I'm in"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member == null) {
            event
                    .replyEmbeds(Embeds.error("Could not get the author of the message"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member.getVoiceState() == null) {
            event
                    .replyEmbeds(Embeds.error("You are not connected to a Voicechannel"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (member.getVoiceState().getChannel() == null) {
            event
                    .replyEmbeds(Embeds.error("You are not connected to a Voicechannel"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        AudioManager manager;
        try {
            manager = getManager(applicationId, guild);
        } catch (KeyException e) {
            event
                    .replyEmbeds(Embeds.error("Could not get an AudioManager"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        final AudioPlayer player;
        AudioPlayer player1;
        try {
            player1 = PlayerVault
                    .getInstance()
                    .getPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        } catch (GuildNotFound | ChannelNotFound e) {
            player1 = PlayerManager
                    .getInstance()
                    .createPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        }
        player = player1;
        AudioSendHandler handler = manager.getSendingHandler();
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }
        // TODO: Link interpretation
        // TODO: Soundcloud / Spotify Api to search
        // TODO: Play the song / add it to the queue
        manager.openAudioConnection(member.getVoiceState().getChannel());
        PlayerManager.getInstance().getManager().loadItem(String.format("%s", url), new AudioResultHandler(player, event, url));
    }
}
