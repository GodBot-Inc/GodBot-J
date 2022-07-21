package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.lavaplayer.AudioResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
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
        playerManager.setFrameBufferDuration((int) TimeUnit.SECONDS.toMillis(10));
        // set to 500 if bad
        playerManager.setItemLoaderThreadPoolSize(1000);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    private AudioPlayerExtender createPlayer(JDA bot, Guild guild, VoiceChannel voiceChannel) {
        PlayerVault vault = PlayerVault.getInstance();
        AudioPlayerExtender player =
                new AudioPlayerExtender(
                        playerManager.createPlayer(),
                        voiceChannel,
                        guild.getAudioManager()
                );
        vault.storePlayer(bot, guild.getId(), player);
        return player;
    }

    public AudioPlayerExtender getOrCreatePlayer(JDA bot, Guild guild, VoiceChannel voiceChannel) {
        AudioPlayerExtender player = PlayerVault.getInstance().getPlayer(bot, guild.getId());
        if (player == null) {
            player = createPlayer(
                    bot,
                    guild,
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

    public static AudioPlayerManagerWrapper getInstance() {
        return managerObj;
    }
}
