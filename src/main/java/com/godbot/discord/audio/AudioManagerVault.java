package discord.audio;

import discord.JDAManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.customExceptions.JDANotFoundException;
import utils.logging.LoggerContent;
import utils.customExceptions.audio.ApplicationNotFoundException;
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
                        "info",
                        "AudioManagerVault-registerJDA",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                        }}
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
                        "info",
                        "AudioManagerVault-registerGuild",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                            put("GuildId", guild.getId());
                            put("GuildName", guild.getName());
                        }}
                )
        );
    }

    public void removeJDA(String applicationId)
            throws ApplicationNotFoundException {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFoundException("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.remove(applicationId);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "AudioManagerVault-removeJDA",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                        }}
                )
        );
    }

    public void removeGuild(String applicationId, String guildId)
            throws ApplicationNotFoundException {
        if (!audioManagerStorage.containsKey(applicationId)) {
            throw new ApplicationNotFoundException("Could not find applicationId in storage " + applicationId);
        }
        audioManagerStorage.get(applicationId).remove(guildId);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "AudioManagerVault-removeGuild",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                            put("GuildId", guildId);
                        }}
                )
        );
    }

    public AudioManager getAudioManager(String applicationId, String guildId)
            throws JDANotFoundException {
        if (!audioManagerStorage.containsKey(applicationId)) {
            JDAManager jdaManager = JDAManager.getInstance();
            JDA godbotJDA = jdaManager.getJDA("godbot");
            registerJDA(applicationId, godbotJDA.getGuilds());
            return getAudioManager(applicationId, guildId);
        }
        if (!audioManagerStorage.get(applicationId).containsKey(guildId)) {
            JDAManager jdaManager = JDAManager.getInstance();
            JDA godbotJDA = jdaManager.getJDA("godbot");
            Guild guild = godbotJDA.getGuildById(guildId);
            registerGuild(applicationId, guild);
            return getAudioManager(applicationId, guildId);
        }
        this.logger.info(
                new LoggerContent(
                        "info",
                        "AudioManagerVault-getAudioManager",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                            put("GuildId", guildId);
                        }}
                )
        );
        return audioManagerStorage.get(applicationId).get(guildId);
    }

    public static AudioManagerVault getInstance() { return managerObj; }
}
