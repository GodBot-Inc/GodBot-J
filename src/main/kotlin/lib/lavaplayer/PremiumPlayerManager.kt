package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import config.premiumEncoding
import config.premiumResamplingQuality
import state.BotSubscriptions
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import state.AudioPlayerExtender
import state.PlayerStorage
import java.util.concurrent.TimeUnit

// Is here to replace AudioPlayerManagerWrapper
// Implement Premium, standard and low versions
object PremiumPlayerManager {
    private val playerManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()

    init {
        playerManager.configuration.resamplingQuality = premiumResamplingQuality
        playerManager.configuration.opusEncodingQuality = premiumEncoding
        playerManager.frameBufferDuration = TimeUnit.SECONDS.toMillis(5).toInt()
        AudioSourceManagers.registerRemoteSources(playerManager)
    }

    private fun createPlayer(guild: Guild, voice: VoiceChannel): AudioPlayerExtender {
        val player = AudioPlayerExtender(
            playerManager.createPlayer(),
            voice,
            guild.audioManager
        )
        PlayerStorage.store(guild.id, player)
        BotSubscriptions.newPlayer(player)
        return player
    }

    fun getOrCreatePlayer(guild: Guild, voice: VoiceChannel) = PlayerStorage.get(guild.id) ?: createPlayer(guild, voice)

    fun loadItem(url: String, callback: AudioResultHandler) = playerManager.loadItem(url, callback)
}
