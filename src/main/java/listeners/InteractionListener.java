package listeners;

import audio.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.customExceptions.ChannelNotFound;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.audio.ApplicationNotFound;
import utils.JDAManager;
import utils.logging.DefaultLoggerClass;
import utils.presets.Embeds;

import javax.annotation.Nonnull;
import java.security.KeyException;
import java.util.HashMap;

public class InteractionListener extends ListenerAdapter {

    private final DefaultLoggerClass logger;

    public InteractionListener() {
        this.logger = new DefaultLoggerClass(this.getClass().getName() + "Logger");
    }

    public HashMap<String, String> getLogArgs(@Nonnull SlashCommandEvent event) {
        // TODO: Finish getLogArgs method :D
        User user = event.getUser();
        Guild guild = event.getGuild();
        if (guild == null) {
            return new HashMap<String, String>() {{
               put("GuildId", "null");
               put("AuthorId", user.getId() );
               put("GuildName", "null");
               put("AuthorName", user.getName() + user.getDiscriminator());
            }};
        }
        return new HashMap<String, String>() {{
            put("GuildId", guild.getId());
            put("AuthorId", user.getId());
            put("GuildName", guild.getName());
            put("AuthorName", user.getName() + user.getDiscriminator());
        }};
    }

    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        this.logger.info("onSlashCommand", getLogArgs(event));
        Dotenv dotenv = Dotenv.load();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                OptionMapping urlParameter = event.getOption("url");
                if (urlParameter == null) {
                    event.
                            replyEmbeds(Embeds.error("You did not pass a url as parameter")).
                            setEphemeral(true).
                            queue();
                    return;
                }
                playCommand(event, dotenv, urlParameter.getAsString());
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

    public void playCommand(SlashCommandEvent event, Dotenv dotenv, String url) {
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
        final AudioPlayer player;
        AudioPlayer player1;
        try {
            player1 = PlayerVault.
                    getInstance().
                    getPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        } catch (GuildNotFound | ChannelNotFound e) {
            player1 = PlayerManager.
                    getInstance().
                    createPlayer(guild.getId(), member.getVoiceState().getChannel().getId());
        }
        player = player1;
        AudioSendHandler handler = manager.getSendingHandler();
        if (handler == null) {
            manager.setSendingHandler(new AudioPlayerSendHandler(player));
        }
        QueueSystem queue = QueueSystem.getInstance();
        // TODO: Link interpretation
        // TODO: Soundcloud / Spotify Api to search
        // TODO: Play the song / add it to the queue
        manager.openAudioConnection(member.getVoiceState().getChannel());
        PlayerManager.getInstance().getManager().loadItem("jPe0bX_GtBo", new AudioResultHandler(player, event, url));
    }
}
