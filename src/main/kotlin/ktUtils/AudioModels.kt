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
    var lastTrack: AudioTrackExtender? = null
    var currentTrack: AudioTrackExtender? = null
    private val audioManager: AudioManager

    private var lifecycle = true
    private var ownCurrentTrack: AudioTrackExtender? = null
    private var lastAction: Long = System.currentTimeMillis()

    init {
        audioPlayer.volume = 50
        audioPlayer.setFrameBufferDuration(5)
        this.audioManager = audioManager
        this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        audioPlayer.addListener(TrackEventListener(this))
        // Execute both tasks async
        thread { currentTrackUpdate() }
        thread { lifecycle() }
    }

    private fun currentTrackUpdate() {
        while (lifecycle) {
            // update current Track if new Track is playing
            if (ownCurrentTrack != null && ownCurrentTrack != currentTrack) {
                lastAction = System.currentTimeMillis()
                lastTrack = ownCurrentTrack
            }
            // Update current Track, if no song is playing
            if (audioPlayer.playingTrack == null && currentTrack != null) {
                lastAction = System.currentTimeMillis()
                lastTrack = currentTrack
                currentTrack = null
            }
            ownCurrentTrack = currentTrack
        }
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

    private fun cleanup() {
        audioManager.closeAudioConnection()
        queue.clear()
        currentTrack = null
        ownCurrentTrack = null
        lastTrack = null
        lastAction = 0
        lifecycle = false
        // only stops the player
        audioPlayer.destroy()
        PlayerVault.getInstance().removePlayer(this)
    }

    fun openConnection() = apply { updateUsage(); if(!audioManager.isConnected) audioManager.openAudioConnection(voiceChannel) }

    fun closeConnection() = apply { audioManager.closeAudioConnection(); cleanup() }

    fun changeChannel(newVoiceChannel: VoiceChannel) = apply { updateUsage(); voiceChannel = newVoiceChannel }

    @Throws(QueueEmptyException::class)
    fun playNext(): PlayableInfo {
        updateUsage()
        if (queue.isEmpty()) {
            throw QueueEmptyException()
        }
        val playableInfo = queue.removeAt(0)
        try {
            playNow(playableInfo)
        } catch (e: GodBotException) {
            return playNext()
        }
        return playableInfo
    }

    @Throws(TrackNotFoundException::class, GodBotException::class)
    fun playNow(playableInfo: PlayableInfo) {
        updateUsage()
        val audioTrack =
            AudioPlayerManagerWrapper
                .getInstance()
                .loadItem(playableInfo.uri)
        currentTrack = AudioTrackExtender(audioTrack, playableInfo)
        audioPlayer.stopTrack()
        // playing a clone of the track, to be sure that the same instance of a track is not played twice, since
        // this would throw an IllegalStateException
        audioPlayer.playTrack(audioTrack)
    }

    @Throws(TrackNotFoundException::class, GodBotException::class)
    fun play(playableInfo: PlayableInfo): Int {
        updateUsage()
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
        if (audioPlayer.playingTrack == null) {
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
            playNow(playableInfo)
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun removeTrackAt(index: Int): PlayableInfo {
        updateUsage()
        return queue.removeAt(index)
    }

    fun setPaused(paused: Boolean): PlayableInfo {
        updateUsage()
        audioPlayer.isPaused = paused
        return currentTrack!!.playableInfo
    }

    fun isPaused() = audioPlayer.isPaused

    fun setVolume(volume: Int) = apply { updateUsage(); audioPlayer.volume = volume }

    fun getVolume() = audioPlayer.volume

    fun clearQueue() = apply { updateUsage();queue.clear() }

    fun stop() = apply { updateUsage();clearQueue(); audioPlayer.stopTrack() }
}

data class AudioTrackExtender(
    val audioTrack: AudioTrack,
    val playableInfo: PlayableInfo
)
