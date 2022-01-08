package singeltons;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import utils.AudioPlayerExtender;
import utils.GuildNotFoundException;
import utils.JDANotFound;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

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
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    private AudioPlayerExtender createPlayer(
            JDA bot, String guildId, VoiceChannel voiceChannel) {
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

    public AudioPlayerExtender getPlayer(
            JDA bot, String guildId, VoiceChannel voiceChannel) {
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

    public AudioPlayerManager getManager() {
        return this.playerManager;
    }

    public static AudioPlayerManagerWrapper getInstance() {
        return managerObj;
    }
}
