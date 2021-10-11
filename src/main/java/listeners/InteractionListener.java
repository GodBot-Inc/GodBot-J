package listeners;

import audio.AudioManagerVault;
import audio.AudioPlayerSendHandler;
import audio.PlayerManager;
import audio.PlayerVault;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.CustomExceptions.ChannelNotFound;
import utils.CustomExceptions.GuildNotFound;
import utils.CustomExceptions.audio.ApplicationNotFound;
import utils.JDAManager;
import utils.loggers.InteractionLogger;
import utils.presets.Embeds;

import javax.annotation.Nonnull;
import java.security.KeyException;
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
        Dotenv dotenv = Dotenv.load();
        event.deferReply().queue();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                playCommand(event, dotenv);
                this.logger.info("PlayCommand", getLogArgs(event));
                break;
        }
    }

    public AudioManager getManager(String applicationId, Guild guild) throws KeyException {
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

    public void playCommand(SlashCommandEvent event, Dotenv dotenv) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        String applicationId = dotenv.get("APPLICATIONID");
        if (applicationId == null) {
            event.
                    replyEmbeds(Embeds.error("Could not get the ApplicationId")).
                    setEphemeral(true).
                    queue();
            return;
        }
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
        AudioManager manager;
        try {
            manager = getManager(applicationId, guild);
        } catch (KeyException e) {
            event.
                    replyEmbeds(Embeds.error("Could not get an AudioManager")).
                    setEphemeral(true).
                    queue();
            return;
        }
        event.deferReply().queue();
        AudioSendHandler handler = manager.getSendingHandler();
        AudioPlayer player;
        try {
            player = PlayerVault.
                    getInstance().
                    getPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        } catch (GuildNotFound | ChannelNotFound e) {
            player = PlayerManager.
                    getInstance().
                    createPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        }
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }

    }
}
