package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.lavaplayer.AudioResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;
import objects.AudioPlayerExtender;

import java.util.concurrent.TimeUnit;

/**
 * A little confusing but essentially a collection of useful functions related to players
 */
public class AudioPlayerManagerWrapper {

    private static final AudioPlayerManagerWrapper managerObj = new AudioPlayerManagerWrapper();
    private final DefaultAudioPlayerManager playerManager;

    private AudioPlayerManagerWrapper() {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        playerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        // set to 1000 if bad (ms not s)
        // Should preload 1 Minute and 20 Seconds
        playerManager.setFrameBufferDuration((int) TimeUnit.SECONDS.toMillis(80));
        // set to 500 if bad
        playerManager.setItemLoaderThreadPoolSize(1000);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    private AudioPlayerExtender createPlayer(JDA bot, String guildId, VoiceChannel voiceChannel) {
        PlayerVault vault = PlayerVault.getInstance();
        AudioPlayerExtender player =
                new AudioPlayerExtender(
                        playerManager.createPlayer(),
                        voiceChannel,
                        AudioManagerVault
                                .getInstance()
                                .getAudioManager(
                                        bot,
                                        guildId
                                )
                );
        vault.storePlayer(bot, guildId, player);
        return player;
    }

    public AudioPlayerExtender getOrCreatePlayer(JDA bot, String guildId, VoiceChannel voiceChannel) {
        AudioPlayerExtender player = PlayerVault.getInstance().getPlayer(bot, guildId);
        if (player == null) {
            player = createPlayer(
                    bot,
                    guildId,
                    voiceChannel
            );
        }
        return player;
    }

    public void loadItem(String url, AudioResultHandler resultHandler) {
        playerManager.loadItem(
                url,
                resultHandler
        );
    }

    public AudioPlayerManager getManager() {
        return this.playerManager;
    }

    public static AudioPlayerManagerWrapper getInstance() {
        return managerObj;
    }
}
