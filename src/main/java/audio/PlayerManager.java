package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import utils.CustomExceptions.ChannelNotFound;
import utils.CustomExceptions.GuildNotFound;
import utils.CustomExceptions.audio.PlayerNotFound;
import utils.loggers.DefaultLogger;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class PlayerManager {

    private static final PlayerManager managerObj = new PlayerManager();
    private final DefaultAudioPlayerManager playerManager;
    private final DefaultLogger logger;

    private PlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        logger = new DefaultLogger(this.getClass().getName() + "Logger");
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

    public static PlayerManager getInstance() {
        return managerObj;
    }
}
