package jdaListeners;

import io.github.cdimascio.dotenv.Dotenv;
import lib.AudioPlayerExtender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import singeltons.JDAManager;
import singeltons.PlayerVault;

public class GeneralListener extends ListenerAdapter {

    private final Dotenv dotenv = Dotenv.load();
    private final String applicationId = dotenv.get("APPLICATIONID");

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        AudioPlayerExtender audioPlayerExtender = PlayerVault.getInstance().getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                event.getGuild().getId()
        );
        if (audioPlayerExtender == null) {
           return;
        }

        if (event.getMember().getId().equals(applicationId)) {
            audioPlayerExtender.changeChannel(event.getChannelJoined());
        }
        if (event.getChannelLeft() == audioPlayerExtender.getVoiceChannel() &&
                // if only the bot is connected to the channel
            event.getChannelLeft().getMembers().size() == 1) {
            audioPlayerExtender.setPaused(true);
        }
        if (event.getChannelJoined() == audioPlayerExtender.getVoiceChannel() &&
            // if only the joined member and the bot are connected to the channel
            event.getChannelJoined().getMembers().size() == 2) {
            audioPlayerExtender.setPaused(false);
        }
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);

        AudioPlayerExtender audioPlayer = PlayerVault.getInstance().getPlayer(godbotJDA, event.getGuild().getId());
        if (audioPlayer == null) {
            return;
        }

        if (event.getChannelJoined() == audioPlayer.getVoiceChannel() &&
            // If only the joined member and the bot are connected to the channel
            event.getChannelJoined().getMembers().size() == 2) {
            audioPlayer.setPaused(false);
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        JDA godbotJDA = JDAManager.getInstance().getJDA(applicationId);
        if (godbotJDA == null) {
            return;
        }

        AudioPlayerExtender audioPlayer = PlayerVault.getInstance().getPlayer(godbotJDA, event.getGuild().getId());
        if (audioPlayer == null) {
            return;
        }

        if (event.getMember().getId().equals(godbotJDA.getSelfUser().getId())) {
            audioPlayer.cleanup();
            return;
        }
        if (event.getChannelLeft() == audioPlayer.getVoiceChannel() &&
            // if only the bot is connected to the channel
            event.getChannelLeft().getMembers().size() == 1 &&
            // If the godbot is not the one who left the channel
            !event.getMember().getId().equals(godbotJDA.getSelfUser().getId())) {
            audioPlayer.setPaused(true);
        }
    }
}
