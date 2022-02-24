package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ktUtils.*;
import lavaplayerHandlers.AudioResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

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
        // Should preload 1 Minute and 20 Seconds of audio (incredibly memory intensive, but reduces shattering)
        // TODO The bot only takes 200 milliseconds of preloading max (implement your own DefaultAudioPlayerManager)
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

    public AudioPlayerExtender getPlayer(JDA bot, String guildId, VoiceChannel voiceChannel) {
        AudioPlayerExtender player;
        try {
            player = PlayerVault
                    .getInstance()
                    .getPlayer(
                            bot,
                            guildId
                    );
        } catch (JDANotFound | GuildNotFoundException e) {
            player = createPlayer(
                    bot,
                    guildId,
                    voiceChannel
            );
        }
        return player;
    }

    public AudioTrack loadItem(String url)
            throws TrackNotFoundException {
        AudioResultHandler audioResultHandler = new AudioResultHandler();
        playerManager.loadItem(
                url,
                audioResultHandler
        );
        while (audioResultHandler.actionType == 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException ignore) {}
        }

        if (audioResultHandler.actionType == 3 || audioResultHandler.actionType == 4) {
            throw new TrackNotFoundException();
        }
        return audioResultHandler.audioTrack;
    }

    public AudioPlayerManager getManager() {
        return this.playerManager;
    }

    public static AudioPlayerManagerWrapper getInstance() {
        return managerObj;
    }
}
