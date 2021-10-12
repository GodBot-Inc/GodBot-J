package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import utils.LoggerContent;
import utils.customExceptions.ChannelNotFound;
import utils.customExceptions.GuildNotFound;
import utils.logging.AudioLogger;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;


public class PlayerVault {

    private static final PlayerVault vaultObj = new PlayerVault();
    private final AudioLogger logger;
    //                   GuildId        channelId  Destination
    private final HashMap<String, HashMap<String, AudioPlayer>> playerStorage = new HashMap<>();

    private PlayerVault() {
        logger = new AudioLogger(this.getClass().getName() + "Logger");
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
            playerStorage.put(guildID, new HashMap<>());
        }
        if (playerStorage.containsKey(channelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + channelID);
        }
        playerStorage.get(guildID).put(channelID, player);
        this.logger.info(
            new LoggerContent(
                "storePlayer",
                new HashMap<String, String>() {{
                    put("GuildId", guildID);
                    put("channelId", channelID);
                }},
                "info"
            )
        );
    }

    public void changeChannelID(String guildID, String oldChannelID, String newChannelID) throws GuildNotFound, ChannelNotFound, KeyAlreadyExistsException {
        checkGuildAndChannel(guildID, oldChannelID);
        if (playerStorage.get(guildID).containsKey(newChannelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + newChannelID);
        }
        AudioPlayer player = playerStorage.get(guildID).remove(oldChannelID);
        playerStorage.get(guildID).put(newChannelID, player);
    }

    public void removePlayer(String guildID, String channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        playerStorage.get(guildID).remove(channelID);
        this.logger.info(
            new LoggerContent(
                "removePlayer",
                new HashMap<String, String>() {{
                    put("GuildId", guildID);
                    put("channelId", channelID);
                }},
                "info"
            )
        );
    }

    public void removeGuild(String guildID) throws GuildNotFound {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFound("GuildID " + guildID);
        }
        playerStorage.remove(guildID);
        this.logger.info(
            new LoggerContent(
                "PlayerVault-removeGuild",
                new HashMap<String, String>() {{
                    put("GuildId", guildID);
                }},
                "info"
            )
        );
    }

    public AudioPlayer getPlayer(String guildID, String channelID) throws GuildNotFound, ChannelNotFound {
        checkGuildAndChannel(guildID, channelID);
        return playerStorage.get(guildID).get(channelID);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
