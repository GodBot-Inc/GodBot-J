package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import io.github.cdimascio.dotenv.Dotenv;
import utils.CustomExceptions.ChannelNotFound;
import utils.CustomExceptions.GuildNotFound;
import utils.CustomExceptions.audio.PlayerNotFound;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PlayerManager {

    private static final PlayerManager managerObj = new PlayerManager();
    private final DefaultAudioPlayerManager playerManager;
    private Logger logger;

    private Logger getLogger() throws IOException {
        Dotenv env = Dotenv.load();

        String dir = env.get("LOGGER_DIR");

        Logger logger = Logger.getLogger("PlayerManagerLogger");
        FileHandler fh = new FileHandler(dir + "\\PlayerManager.log");
        SimpleFormatter formatter = new SimpleFormatter();

        logger.addHandler(fh);
        fh.setFormatter(formatter);
        return logger;
    }

    private PlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        try {
            logger = getLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AudioPlayer createPlayer(String guildID, String channelID) throws KeyAlreadyExistsException {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        AudioPlayer player = playerManager.createPlayer();
        vault.storePlayer(guildID, channelID, player);
        queue.registerPlayer(player);
        logger.info("Registered new Player " + player + " GuildID " + guildID + " channelID " + channelID);
        return player;
    }

    public void removePlayer(String guildID, String channelID, AudioPlayer player) throws GuildNotFound, ChannelNotFound, PlayerNotFound {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        vault.removePlayer(guildID, channelID);
        queue.removePlayer(player);
        player.stopTrack();
        player.destroy();
    }

    public static PlayerManager getManagerObj() {
        return managerObj;
    }
}
