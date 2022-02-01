package ktUtils

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavaplayerHandlers.AudioPlayerSendHandler
import lavaplayerHandlers.TrackEventListener
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import playableInfo.PlayableInfo
import singeltons.AudioPlayerManagerWrapper
import singeltons.PlayerVault
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class AudioPlayerExtender(
    private val audioPlayer: AudioPlayer,
    var voiceChannel: VoiceChannel,
    audioManager: AudioManager) {

    var loop: Boolean = false
    val queue = ArrayList<PlayableInfo>()
    var currentTrack: PlayableInfo? = null
    private val audioManager: AudioManager

    private var lifecycle = true
    private var lastAction: Long = System.currentTimeMillis()

    init {
        audioPlayer.volume = 50
        audioPlayer.setFrameBufferDuration(200)
        this.audioManager = audioManager
        this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        audioPlayer.addListener(TrackEventListener(this))
        // Execute lifecycle async
        thread { lifecycle() }
    }

    private fun lifecycle() {
        while (lifecycle) {
            // 600000 = 10 min
            // 1800000 = 30 min
            if (System.currentTimeMillis() - lastAction >= 1800000 && checkTrack()) {
                cleanup()
            }
            TimeUnit.SECONDS.sleep(10)
        }
    }

    // Returns whether the player can be cleaned up based on the track state of the audioPlayer
    private fun checkTrack(): Boolean {
        return if (audioPlayer.playingTrack != null && !audioPlayer.isPaused) {
            false
        } else if (audioPlayer.playingTrack != null && audioPlayer.isPaused) {
            true
        } else {
            true
        }
    }

    private fun updateUsage() = apply { lastAction = System.currentTimeMillis() }

    fun cleanup() {
        audioManager.closeAudioConnection()
        queue.clear()
        currentTrack = null
        lastAction = 0
        lifecycle = false
        // only stops the player
        audioPlayer.destroy()
        PlayerVault.getInstance().removePlayer(this)
    }

    fun openConnection() = apply { updateUsage(); if(!audioManager.isConnected) audioManager.openAudioConnection(voiceChannel) }

    fun closeConnection() = apply { audioManager.closeAudioConnection(); cleanup() }

    fun changeChannel(newVoiceChannel: VoiceChannel) = apply { updateUsage(); voiceChannel = newVoiceChannel }

    fun playNowOrNext(playableInfo: PlayableInfo) {
        updateUsage()
        try {
            // Play Now already sets current Track
            playNow(playableInfo)
        } catch (e: TrackNotFoundException) {
            try {
                // Play next already sets currentTrack
                playNext()
            } catch (e: QueueEmptyException) {
                currentTrack = null
            }
        }
    }

    @Throws(QueueEmptyException::class)
    fun playNext(): PlayableInfo {
        updateUsage()
        if (queue.isEmpty()) {
            currentTrack = null
            throw QueueEmptyException()
        }
        val playableInfo = queue.removeAt(0)
        try {
            // Play now already sets currentTrack
            playNow(playableInfo)
        } catch (e: GodBotException) {
            // Recursion, so eventually the method will end
            return playNext()
        }
        // In case of expected method process
        currentTrack = playableInfo
        return playableInfo
    }

    @Throws(TrackNotFoundException::class)
    fun playNow(playableInfo: PlayableInfo) {
        updateUsage()
        val audioTrack =
            AudioPlayerManagerWrapper
                .getInstance()
                .loadItem(playableInfo.uri)

        audioPlayer.stopTrack()
        currentTrack = try {
            audioPlayer.playTrack(audioTrack)
            playableInfo
        } catch (e: Exception) {
            // This should never happen
            e.printStackTrace()
            null
        }
    }

    @Throws(TrackNotFoundException::class)
    fun play(playableInfo: PlayableInfo): Int {
        updateUsage()
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
        if (audioPlayer.playingTrack == null && queue.size == 0) {
            // playNow sets currentTrack
            playNow(playableInfo)
            return 0
        }
        println("Added to Queue: " + playableInfo.title)
        queue.add(playableInfo)
        return queue.size - 1
    }

    @Throws(IndexOutOfBoundsException::class)
    fun skipTo(index: Int) {
        updateUsage()
        if (index < queue.size) {
            val playableInfo = queue[index]
            for (i in 0 until index + 1) {
                queue.removeAt(0)
            }
            // playNowOrNext sets currentTrack
            playNowOrNext(playableInfo)
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun removeTrackAt(index: Int): PlayableInfo {
        updateUsage()
        return queue.removeAt(index)
    }

    fun setPaused(paused: Boolean) {
        updateUsage()
        audioPlayer.isPaused = paused
    }

    fun isPaused() = audioPlayer.isPaused

    fun setVolume(volume: Int) = apply { updateUsage(); audioPlayer.volume = volume }

    fun getVolume() = audioPlayer.volume

    fun isConnected() = audioManager.isConnected

    fun clearQueue() = apply { updateUsage();queue.clear() }

    fun stop() = apply { updateUsage();clearQueue(); audioPlayer.stopTrack() }
}

data class AudioTrackExtender(
    val audioTrack: AudioTrack,
    val playableInfo: PlayableInfo
)
