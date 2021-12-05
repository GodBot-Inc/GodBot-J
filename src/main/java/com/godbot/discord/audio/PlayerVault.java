package com.godbot.discord.audio;

import com.godbot.utils.customExceptions.ChannelNotFoundException;
import com.godbot.utils.customExceptions.GuildNotFoundException;
import com.godbot.utils.logging.AudioLogger;
import com.godbot.utils.logging.LoggerContent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

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

    public void checkGuildAndChannel(
            String guildID,
            String channelID
    ) throws GuildNotFoundException,
            ChannelNotFoundException {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFoundException("GuildID " + guildID);
        } else if (!playerStorage.get(guildID).containsKey(channelID)) {
            throw new ChannelNotFoundException("ChannelID " + channelID);
        }
    }

    public void storePlayer(
            String guildID,
            String channelID,
            AudioPlayer player
    ) throws KeyAlreadyExistsException {
        if (!playerStorage.containsKey(guildID)) {
            playerStorage.put(guildID, new HashMap<>());
        }
        if (playerStorage.containsKey(channelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + channelID);
        }
        playerStorage.get(guildID).put(channelID, player);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "PlayerVault-storePlayer",
                        "",
                        new HashMap<>() {{
                            put("GuildId", guildID);
                            put("channelId", channelID);
                        }}
                )
        );
    }

    public void changeChannelID(
            String guildID,
            String oldChannelID,
            String newChannelID
    ) throws GuildNotFoundException,
            ChannelNotFoundException,
            KeyAlreadyExistsException {
        checkGuildAndChannel(guildID, oldChannelID);
        if (playerStorage.get(guildID).containsKey(newChannelID)) {
            throw new KeyAlreadyExistsException("ChannelID " + newChannelID);
        }
        AudioPlayer player = playerStorage.get(guildID).remove(oldChannelID);
        playerStorage.get(guildID).put(newChannelID, player);
    }

    public void removePlayer(
            String guildID,
            String channelID
    ) throws GuildNotFoundException,
            ChannelNotFoundException {
        checkGuildAndChannel(guildID, channelID);
        playerStorage.get(guildID).remove(channelID);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "PlayerVault-removePlayer",
                        "",
                        new HashMap<>() {{
                            put("GuildId", guildID);
                            put("channelId", channelID);
                        }}
                )
        );
    }

    public void removeGuild(String guildID)
            throws GuildNotFoundException {
        if (!playerStorage.containsKey(guildID)) {
            throw new GuildNotFoundException("GuildID " + guildID);
        }
        playerStorage.remove(guildID);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "PlayerVault-removeGuild",
                        "",
                        new HashMap<>() {{
                            put("GuildId", guildID);
                        }}
                )
        );
    }

    public AudioPlayer getPlayer(
            String guildID,
            String channelID
    ) throws GuildNotFoundException,
            ChannelNotFoundException {
        checkGuildAndChannel(guildID, channelID);
        return playerStorage.get(guildID).get(channelID);
    }

    public static PlayerVault getInstance() { return vaultObj; }
}
