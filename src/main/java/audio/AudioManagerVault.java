package audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.audio.ApplicationNotFound;
import utils.logging.DefaultLoggerClass;

import java.util.HashMap;
import java.util.List;

public class AudioManagerVault {

    private static final AudioManagerVault managerObj = new AudioManagerVault();

    //                ApplicationId       GuildId  Destination
    private final HashMap<String, HashMap<String, AudioManager>> audioManagerStorage = new HashMap<>();
    private final DefaultLoggerClass logger;

    private AudioManagerVault() {
        logger = new DefaultLoggerClass(this.getClass().getName() + "Logger");
    }

    public void registerJDA(String applicationId, List<Guild> guilds) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        for (Guild guild : guilds) {
            audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        }
        logger.info("registerJDA", new HashMap<String, String>() {{ put("ApplicationId", applicationId); }});
    }

    public void registerGuild(String applicationId, Guild guild) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        logger.info("registerGuild", new HashMap<String, String>() {{ put("GuildId", guild.getId()); }});
    }

    public void removeJDA(String applicationId) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.remove(applicationId);
        logger.info("removeJDA", new HashMap<String, String>() {{ put("ApplicationId", applicationId); }});
    }

    public void removeGuild(String applicationId, String guildId) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.get(applicationId).remove(guildId);
        logger.info("removeGuild", new HashMap<String, String>() {{ put("ApplicationId", applicationId); }});
    }

    public AudioManager getAudioManager(String applicationId, String guildId) throws ApplicationNotFound, GuildNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        if (!audioManagerStorage.get(applicationId).containsKey(guildId)) {
            throw new GuildNotFound("Could not find guildId in storage " + guildId);
        }
        logger.info("getAudioManager", new HashMap<String, String>() {{
            put("ApplicationId", applicationId);
            put("GuildId", guildId);
        }});
        return audioManagerStorage.get(applicationId).get(guildId);
    }

    public static AudioManagerVault getInstance() { return managerObj; }
}
