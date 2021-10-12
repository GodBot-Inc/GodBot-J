package audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.LoggerContent;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.audio.ApplicationNotFound;
import utils.logging.AudioLogger;

import java.util.HashMap;
import java.util.List;

public class AudioManagerVault {

    private static final AudioManagerVault managerObj = new AudioManagerVault();

    //                ApplicationId       GuildId  Destination
    private final HashMap<String, HashMap<String, AudioManager>> audioManagerStorage = new HashMap<>();
    private final AudioLogger logger;

    private AudioManagerVault() {
        this.logger = new AudioLogger(this.getClass().getName() + "Logger");
    }

    public void registerJDA(String applicationId, List<Guild> guilds) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        for (Guild guild : guilds) {
            audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        }
        this.logger.info(
            new LoggerContent(
                "registerJDA",
                new HashMap<String, String>() {{
                    put("applicationId", applicationId);
                }},
                "info"
            )
        );
    }

    public void registerGuild(String applicationId, Guild guild) {
        if (!audioManagerStorage.containsKey(applicationId)) {
            audioManagerStorage.put(applicationId, new HashMap<>());
        }
        audioManagerStorage.get(applicationId).put(guild.getId(), guild.getAudioManager());
        this.logger.info(
            new LoggerContent(
                "registerGuild",
                new HashMap<String, String>() {{
                    put("appicationId", applicationId);
                    put("GuildId", guild.getId());
                    put("GuildName", guild.getName());
                }},
                "info"
            )
        );
    }

    public void removeJDA(String applicationId) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.remove(applicationId);
        this.logger.info(
            new LoggerContent(
                "removeJDA",
                new HashMap<String, String>() {{
                    put("applicationId", applicationId);
                }},
                "info"
            )
        );
    }

    public void removeGuild(String applicationId, String guildId) throws ApplicationNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.get(applicationId).remove(guildId);
        this.logger.info(
            new LoggerContent(
                "removeGuild",
                new HashMap<String, String>() {{
                    put("applicationId", applicationId);
                    put("GuildId", guildId);
                }},
                "info"
            )
        );
    }

    public AudioManager getAudioManager(String applicationId, String guildId) throws ApplicationNotFound, GuildNotFound {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFound("Could not find applicationId in storage " + applicationId);
        }
        if (!audioManagerStorage.get(applicationId).containsKey(guildId)) {
            throw new GuildNotFound("Could not find guildId in storage " + guildId);
        }
        this.logger.info(
            new LoggerContent(
                "getAudioManager",
                new HashMap<String, String>() {{
                    put("applicaitonId", applicationId);
                    put("GuildId", guildId);
                }},
                "info"
            )
        );
        return audioManagerStorage.get(applicationId).get(guildId);
    }

    public static AudioManagerVault getInstance() { return managerObj; }
}
