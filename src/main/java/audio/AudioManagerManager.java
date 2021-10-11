package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.CustomExceptions.GuildNotFound;
import utils.CustomExceptions.audio.ApplicationNotFound;
import utils.loggers.AudioManagerManagerLogger;

import java.security.KeyException;
import java.util.HashMap;
import java.util.List;

public class AudioManagerManager {

    private static final AudioManagerManager managerObj = new AudioManagerManager();

    //                ApplicationId       GuildId  Destination
    private final HashMap<String, HashMap<String, AudioManager>> audioManagerStorage = new HashMap<>();
    private final AudioManagerManagerLogger logger;

    private AudioManagerManager() {
        logger = AudioManagerManagerLogger.getInstance();
    }

    public void registerJDA(String applicationId, List<Guild> guilds) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        for (Guild guild : guilds) {
            audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        }
        logger.info("registerJDA" + "|ApplicationId-" + applicationId);
    }

    public void registerGuild(String applicationId, Guild guild) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        logger.info("registerGuild" + "|GuildId-" + guild.getId());
    }

    public void removeJDA(String applicationId) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.remove(applicationId);
        logger.info("removeJDA" + "|ApplicationId-" + applicationId);
    }

    public void removeGuild(String applicationId, Guild guild) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.remove(applicationId);
        logger.info("removeGuild" + "|ApplicationId-" + applicationId);
    }

    public AudioManager getAudioManager(String applicationId, String guildId) throws ApplicationNotFound, GuildNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        if (!audioManagerStorage.get(applicationId).containsKey(guildId)) {
            throw new GuildNotFound("Could not find guildId in storage " + guildId);
        }
        logger.info("getAudioManager" + "|ApplicationId-" + applicationId + "|GuildId-" + guildId);
        return audioManagerStorage.get(applicationId).get(guildId);
    }

    public static AudioManagerManager getInstance() { return managerObj; }
}
