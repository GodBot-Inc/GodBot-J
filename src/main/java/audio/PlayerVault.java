package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.github.cdimascio.dotenv.Dotenv;
import utils.CustomExceptions.ChannelNotFound;
import utils.CustomExceptions.GuildNotFound;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


class PlayerVault {
    /**
     * This Singelton class takes care of player storage. It stores all of them according to their guildID and channelID
     */
    private static final PlayerVault vaultObj = new PlayerVault();
    private Logger logger;
    private final HashMap<Integer, HashMap<Integer, AudioPlayer>> playerStorage = new HashMap<>();

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

    public void checkGuildAndChannel(Integer guildID, Integer channelID) throws GuildNotFound, ChannelNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        } else if (!playerStorage.get(guildID).containsKey(channelID)) {
            throw new ChannelNotFound("ChannelID " + channelID);
        }
    }

    public void addGuild(Integer guildID) {
        HashMap<Integer, AudioPlayer> innerMap = new HashMap<>();
        playerStorage.put(guildID, innerMap);
        logger.info("Added new GuildID " + guildID);
    }

    public void storePlayer(Integer guildID, Integer channelID, AudioPlayer player) throws GuildNotFound, ChannelNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        }
        if (playerStorage.containsKey(channelID)) {
            throw new ChannelNotFound("ChannelID " + channelID);
        }
        playerStorage.get(guildID).put(channelID, player);
        logger.info("Stored new player | GuildID " + guildID + " ChannelID" + channelID);
    }

    public void changeChannelID(Integer guildID, Integer oldChannelID, Integer newChannelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, oldChannelID);
        AudioPlayer player = playerStorage.get(guildID).get(oldChannelID);
        playerStorage.get(guildID).remove(oldChannelID);
        playerStorage.get(guildID).put(newChannelID, player);
        logger.info("Changed player ChannelID | GuildID " + guildID + " oldChannelID " + oldChannelID + " newChannelID " + newChannelID);
    }

    public void removePlayer(Integer guildID, Integer channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        playerStorage.get(guildID).remove(channelID);
        logger.info("Removed Player | GuildID " + guildID + " channelID " + channelID);
    }

    public void removeGuild(Integer guildID) throws GuildNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        }
        playerStorage.remove(guildID);
        logger.info("Removed Guild | GuildID " + guildID);
    }

    public AudioPlayer getPlayer(int guildID, Integer channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        return playerStorage.get(guildID).get(channelID);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
