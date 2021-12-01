package discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord.audio.lavaplayer.TrackEventListener;
import utils.logging.LoggerContent;
import utils.customExceptions.ChannelNotFoundException;
import utils.customExceptions.GuildNotFoundException;
import utils.customExceptions.audio.PlayerNotFoundException;
import utils.logging.AudioLogger;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;

/**
 * A little confusing but essentially a collection of useful functions related to players
 */
public class PlayerManager {

    private static final PlayerManager managerObj = new PlayerManager();
    private final DefaultAudioPlayerManager playerManager;
    private final AudioLogger logger;

    private PlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        logger = new AudioLogger(this.getClass().getName() + "Logger");
    }

    public AudioPlayer createPlayer(String guildId, String channelId) throws KeyAlreadyExistsException {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        AudioPlayer player = playerManager.createPlayer();
        player.addListener(new TrackEventListener());
        vault.storePlayer(guildId, channelId, player);
        queue.registerPlayer(player);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "PlayerManager-createPlayer",
                        "",
                        new HashMap<>() {{
                            put("GuildId", guildId);
                            put("channelId", channelId);
                        }}
                )
        );
        return player;
    }

    public void removePlayer(String guildID, String channelID, AudioPlayer player) throws GuildNotFoundException, ChannelNotFoundException, PlayerNotFoundException {
        PlayerVault vault = PlayerVault.getInstance();
        QueueSystem queue = QueueSystem.getInstance();
        vault.removePlayer(guildID, channelID);
        queue.removePlayer(player);
        player.destroy();
        this.logger.info(
                new LoggerContent(
                        "info",
                        "PlayerManager-removePlayer",
                        "",
                        new HashMap<>() {{
                            put("GuildId", guildID);
                            put("channelId", channelID);
                        }}
                )
        );
    }

    /**
     * If the player is not registered we have to register it and rerun the try block
     * and so on that would be an infinite loop without recursion
     * @param audioTrack The audioTrack that should be played
     * @return Returns if the player is now playing or if the song got appended to the queue
     */
    public static boolean playTrack(AudioPlayer player, AudioTrack audioTrack) {
        QueueSystem queueSystem = QueueSystem.getInstance();
        try {
            if (player.getPlayingTrack() != null) {
                queueSystem.addTrack(player, audioTrack);
                return false;
            } else {
                player.playTrack(audioTrack);
                return true;
            }
        } catch(PlayerNotFoundException e) {
            queueSystem.registerPlayer(player);
            return playTrack(player, audioTrack);
        }
    }

    public void stopPlayer(AudioPlayer player) {
        player.stopTrack();
        try {
            QueueSystem
                    .getInstance()
                    .clearQueue(player);
        } catch (PlayerNotFoundException ignore) {}
    }

    public AudioPlayerManager getManager() {
        return this.playerManager;
    }

    public static PlayerManager getInstance() {
        return managerObj;
    }
}
