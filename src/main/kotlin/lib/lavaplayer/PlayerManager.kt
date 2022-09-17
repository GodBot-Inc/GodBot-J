package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import config.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import state.AudioPlayerExtender
import state.BotSubscriptions
import state.PlayerStorage
import java.util.concurrent.Future

// Is here to replace AudioPlayerManagerWrapper
// Implement Premium, standard and low versions
object PlayerManager {
    private val playerManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()

    init {
        playerManager.configuration.resamplingQuality = mediumResamplingQuality
        playerManager.configuration.opusEncodingQuality = mediumEncoding
        playerManager.frameBufferDuration = mediumBuffering
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

    fun setPremium() {
        playerManager.configuration.resamplingQuality = premiumResamplingQuality
        playerManager.configuration.opusEncodingQuality = premiumEncoding
        playerManager.frameBufferDuration = premiumBuffering
    }

    fun setMedium() {
        playerManager.configuration.resamplingQuality = mediumResamplingQuality
        playerManager.configuration.opusEncodingQuality = mediumEncoding
        playerManager.frameBufferDuration = mediumBuffering
    }

    fun setLow() {
        playerManager.configuration.resamplingQuality = lowResamplingQuality
        playerManager.configuration.opusEncodingQuality = lowEncoding
        playerManager.frameBufferDuration = lowBuffering
    }

    fun getOrCreatePlayer(guild: Guild, voice: VoiceChannel) = PlayerStorage.get(guild.id) ?: createPlayer(guild, voice)

    fun loadItem(url: String, callback: AudioResultHandler): Future<Void> = playerManager.loadItem(url, callback)
}
