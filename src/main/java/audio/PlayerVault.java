package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import io.github.cdimascio.dotenv.Dotenv;
import utils.CustomExceptions.ChannelNotFound;
import utils.CustomExceptions.GuildNotFound;
import utils.CustomExceptions.audio.PlayerNotFound;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


class PlayerVault {

    private static final PlayerVault vaultObj = new PlayerVault();
    private Logger logger;
    //                   GuildId        channelId  Destination
    private final HashMap<String, HashMap<String, AudioPlayer>> playerStorage = new HashMap<>();

    private Logger getLogger() throws IOException {
        Dotenv env = Dotenv.load();

        String dir = env.get("LOGGER_DIR");

        Logger logger = Logger.getLogger("PlayerVaultLogger");
        FileHandler fh = new FileHandler(dir + "\\PlayerVault.log");
        SimpleFormatter formatter = new SimpleFormatter();

        logger.addHandler(fh);
        fh.setFormatter(formatter);
        return logger;
    }

    private PlayerVault() {
        try {
            logger = getLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkGuildAndChannel(String guildID, String channelID) throws GuildNotFound, ChannelNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        } else if (!playerStorage.get(guildID).containsKey(channelID)) {
            throw new ChannelNotFound("ChannelID " + channelID);
        }
    }

    public void storePlayer(String guildID, String channelID, AudioPlayer player) throws KeyAlreadyExistsException {
        if (!playerStorage.containsKey(guildID)) {
            playerStorage.put(guildID, new HashMap<String, AudioPlayer>());
            logger.info("Added new GuildID " + guildID);
        }
        if (playerStorage.containsKey(channelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + channelID);
        }
        playerStorage.get(guildID).put(channelID, player);
        logger.info("Stored new player | GuildID " + guildID + " ChannelID" + channelID);
    }

    public void changeChannelID(String guildID, String oldChannelID, String newChannelID) throws GuildNotFound, ChannelNotFound, KeyAlreadyExistsException {
        checkGuildAndChannel(guildID, oldChannelID);
        if (playerStorage.get(guildID).containsKey(newChannelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + newChannelID);
        }
        AudioPlayer player = playerStorage.get(guildID).remove(oldChannelID);
        playerStorage.get(guildID).put(newChannelID, player);
        logger.info("Changed player ChannelID | GuildID " + guildID + " oldChannelID " + oldChannelID + " newChannelID " + newChannelID);
    }

    public void removePlayer(String guildID, String channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        playerStorage.get(guildID).remove(channelID);
        logger.info("Removed Player | GuildID " + guildID + " channelID " + channelID);
    }

    public void removeGuild(String guildID) throws GuildNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        }
        playerStorage.remove(guildID);
        logger.info("Removed Guild | GuildID " + guildID);
    }

    public AudioPlayer getPlayer(String guildID, String channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        return playerStorage.get(guildID).get(channelID);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
