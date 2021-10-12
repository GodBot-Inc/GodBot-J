package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import utils.customExceptions.ChannelNotFound;
import utils.customExceptions.GuildNotFound;
import utils.customExceptions.audio.PlayerNotFound;
import utils.logging.DefaultLoggerClass;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;

public class PlayerManager {

    private static final PlayerManager managerObj = new PlayerManager();
    private final DefaultAudioPlayerManager playerManager;
    private final DefaultLoggerClass logger;

    private PlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        logger = new DefaultLoggerClass(this.getClass().getName() + "Logger");
    }

    public AudioPlayer createPlayer(String guildId, String channelId) throws KeyAlreadyExistsException {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        AudioPlayer player = playerManager.createPlayer();
        vault.storePlayer(guildId, channelId, player);
        queue.registerPlayer(player);
        logger.info("createPlayer", new HashMap<String, String>() {{
            put("GuildId", guildId);
            put("channelId", channelId);
        }});
        return player;
    }

    public void removePlayer(String guildID, String channelID, AudioPlayer player) throws GuildNotFound, ChannelNotFound, PlayerNotFound {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        vault.removePlayer(guildID, channelID);
        queue.removePlayer(player);
        player.destroy();
        logger.info("removePlayer", new HashMap<String, String>() {{
            put("GuildId", guildID);
            put("channelId", channelID);
        }});
    }

    public AudioPlayerManager getManager() {
        return this.playerManager;
    }

    public static PlayerManager getInstance() {
        return managerObj;
    }
}
