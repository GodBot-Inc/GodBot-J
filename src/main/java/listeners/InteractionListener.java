package listeners;

import audio.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import utils.loggers.DefaultLogger;
import utils.presets.Embeds;

import javax.annotation.Nonnull;
import java.security.KeyException;
import java.util.HashMap;
import java.util.Objects;

public class InteractionListener extends ListenerAdapter {

    private final DefaultLogger logger;

    public InteractionListener() {
        this.logger = new DefaultLogger(this.getClass().getName() + "Logger");
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
        this.logger.info("onSlashCommand", getLogArgs(event));
        Dotenv dotenv = Dotenv.load();
        event.deferReply().queue();
        if (event.getGuild() == null) { return; }
        switch (event.getName()) {
            case "play":
                String url = Objects.requireNonNull(event.getOption("url")).getAsString();
                playCommand(event, dotenv, url);
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
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        QueueSystem queue = QueueSystem.getInstance();
        // TODO: Link interpretation
        // TODO: Load tracks according to the source of the link
        // TODO: Play the song / add it to the queue
        // Connect to a Voicechannel
        manager.openAudioConnection(member.getVoiceState().getChannel());
        playerManager.loadItem("scsearch:Industry Baby", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                player.playTrack(audioTrack);
                System.out.println(audioTrack);
                System.out.println(audioTrack.getPosition());
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("oh no playlist");
            }

            @Override
            public void noMatches() {
                System.out.println("No matches");
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println("Loading failed");
            }
        });
    }
}
