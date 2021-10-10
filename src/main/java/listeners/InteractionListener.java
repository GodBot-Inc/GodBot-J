package listeners;

import audio.AudioPlayerSendHandler;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;

public class InteractionListener extends ListenerAdapter {
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        /* TODO: event.reply().setEphemeral(true).queue(); used if only the user who used the slash command should see
            the response message (in case of an error)
         */
        event.deferReply().queue();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                playCommand(event);
                break;
        }
    }

    public void playCommand(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        assert guild != null;
        AudioManager manager = guild.getAudioManager();
        AudioSendHandler handler = manager.getSendingHandler();
        PlayerManager playerManager = PlayerManager.getManagerObj();
        // TODO: Null safe
        AudioPlayer player = playerManager.createPlayer(Integer.parseInt(guild.getId()), Integer.parseInt(event.getMember().getVoiceState().getChannel().getId()));
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }
    }
}
