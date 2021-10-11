package listeners;

import audio.AudioManagerManager;
import audio.AudioPlayerSendHandler;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.loggers.AudioManagerManagerLogger;
import utils.loggers.InteractionLogger;
import utils.presets.Embeds;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class InteractionListener extends ListenerAdapter {

    private final InteractionLogger logger;

    public InteractionListener() {
        this.logger = InteractionLogger.getInstance();
    }

    public HashMap<String, String> getLogArgs(@Nonnull SlashCommandEvent event) {
        // TODO: Finish getLogArgs method :D
        User user = event.getUser();
        Guild guild = event.getGuild();
        assert guild != null;
        return new HashMap<String, String>() {{
            put("GuildId", guild.getId());
            put("AuthorId", user.getId());
            put("GuildName", guild.getName());
            put("AuthorName", user.getName() + user.getDiscriminator());
        }};
    }

    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        /* TODO: event.reply().setEphemeral(true).queue(); used if only the user who used the slash command should see
            the response message (in case of an error)
         */
        event.deferReply().queue();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                playCommand(event);
                this.logger.info("PlayCommand", getLogArgs(event));
                break;
        }
    }

    public void playCommand(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (guild == null) {
            event.
                    replyEmbeds(Embeds.error("Could not get the Guild I'm in")).
                    setEphemeral(true).
                    queue();
            return;
        }
        if (member == null) {
            event.
                    replyEmbeds(Embeds.error("Could not get the author of the message")).
                    setEphemeral(true).
                    queue();
            return;
        }
        if (member.getVoiceState() == null) {
            event.
                    replyEmbeds(Embeds.error("You are not connected to a Voicechannel")).
                    setEphemeral(true).
                    queue();
            return;
        }
        if (member.getVoiceState().getChannel() == null) {
            event.
                    replyEmbeds(Embeds.error("You are not connected to a Voicechannel")).
                    setEphemeral(true).
                    queue();
            return;
        }
        AudioManagerManager audioManager = AudioManagerManager.getInstance();
        AudioManager manager = guild.getAudioManager();
        AudioSendHandler handler = manager.getSendingHandler();
        PlayerManager playerManager = PlayerManager.getManagerObj();
        AudioPlayer player = playerManager.createPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }
    }
}
