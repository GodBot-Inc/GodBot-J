package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lib.lavaplayer.AudioResultHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import objects.AudioPlayerExtender;
import state.PlayerStorage;

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

    private AudioPlayerExtender createPlayer(Guild guild, VoiceChannel voiceChannel) {
        AudioPlayerExtender player =
                new AudioPlayerExtender(
                        playerManager.createPlayer(),
                        voiceChannel,
                        guild.getAudioManager()
                );
        PlayerStorage.INSTANCE.store(guild.getId(), player);
        return player;
    }

    public AudioPlayerExtender getOrCreatePlayer(Guild guild, VoiceChannel voiceChannel) {
        AudioPlayerExtender player = PlayerStorage.INSTANCE.get(guild.getId());
        if (player == null) {
            player = createPlayer(
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
