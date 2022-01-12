package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lavaplayerHandlers.AudioPlayerSendHandler;
import logging.AudioLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import ktUtils.ApplicationNotFoundException;

import java.util.HashMap;
import java.util.List;

public class AudioManagerVault {

    private static final AudioManagerVault managerObj = new AudioManagerVault();

    //                   BotJDA        GuildId  Destination
    private final HashMap<JDA, HashMap<String, AudioManager>> audioManagerStorage = new HashMap<>();
    private final AudioLogger logger;

    private AudioManagerVault() {
        this.logger = new AudioLogger(this.getClass().getName() + "Logger");
    }

    public void registerJDA(JDA botJDA, List<Guild> guilds) {
        if (!audioManagerStorage.containsKey(botJDA)) {
            audioManagerStorage.put(botJDA, new HashMap<>());
        }
        for (Guild guild : guilds) {
            audioManagerStorage.get(botJDA).put(guild.getId(), guild.getAudioManager());
        }
//        this.logger.info(
//                new LoggerContent(
//                        "info",
//                        "AudioManagerVault-registerJDA",
//                        "",
//                        new HashMap<>()
//                )
//        );
    }

    public void registerGuild(JDA botJDA, Guild guild) {
        if (!audioManagerStorage.containsKey(botJDA)) {
            audioManagerStorage.put(botJDA, new HashMap<>());
        }
        audioManagerStorage.get(botJDA).put(guild.getId(), guild.getAudioManager());
//        this.logger.info(
//                new LoggerContent(
//                        "info",
//                        "AudioManagerVault-registerGuild",
//                        "",
//                        new HashMap<>() {{
//                            put("GuildId", guild.getId());
//                            put("GuildName", guild.getName());
//                        }}
//                )
//        );
    }

    public void removeJDA(JDA botJDA) {
        audioManagerStorage.remove(botJDA);
//        this.logger.info(
//                new LoggerContent(
//                        "info",
//                        "AudioManagerVault-removeJDA",
//                        "",
//                        new HashMap<>()
//                )
//        );
    }

    public void removeGuild(JDA botJDA, String guildId)
            throws ApplicationNotFoundException {
        if (!audioManagerStorage.containsKey(botJDA)) {
            throw new ApplicationNotFoundException();
        }
        audioManagerStorage.get(botJDA).remove(guildId);
//        this.logger.info(
//                new LoggerContent(
//                        "info",
//                        "AudioManagerVault-removeGuild",
//                        "",
//                        new HashMap<>() {{
//                            put("GuildId", guildId);
//                        }}
//                )
//        );
    }

    public AudioManager getAudioManager(JDA botJDA, String guildId) {
        if (!audioManagerStorage.containsKey(botJDA)) {
            registerJDA(botJDA, botJDA.getGuilds());
            return getAudioManager(botJDA, guildId);
        }
        if (!audioManagerStorage.get(botJDA).containsKey(guildId)) {
            Guild guild = botJDA.getGuildById(guildId);
            registerGuild(botJDA, guild);
            return getAudioManager(botJDA, guildId);
        }
//        this.logger.info(
//                new LoggerContent(
//                        "info",
//                        "AudioManagerVault-getAudioManager",
//                        "",
//                        new HashMap<>() {{
//                            put("GuildId", guildId);
//                        }}
//                )
//        );
        return audioManagerStorage.get(botJDA).get(guildId);
    }

    public void checkSendingHandler(JDA botJDA, String guildId, AudioPlayer player) {
        AudioManager audioManager;
        if (!audioManagerStorage.containsKey(botJDA)) {
            registerJDA(botJDA, botJDA.getGuilds());
        }
        if (!audioManagerStorage.get(botJDA).containsKey(guildId)) {
            Guild guild = botJDA.getGuildById(guildId);
            registerGuild(botJDA, guild);
        }
        audioManager = getAudioManager(botJDA, guildId);

        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public static AudioManagerVault getInstance() { return managerObj; }
}
