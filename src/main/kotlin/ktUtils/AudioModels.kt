package ktUtils

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavaplayerHandlers.AudioPlayerSendHandler
import lavaplayerHandlers.TrackEventListener
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import playableInfo.PlayableInfo
import singeltons.AudioPlayerManagerWrapper
import java.util.concurrent.Executors

class AudioPlayerExtender(
    val audioPlayer: AudioPlayer,
    var voiceChannel: VoiceChannel,
    audioManager: AudioManager) {

    var loop: Boolean = false
    var shuffle: Boolean = false
    var queue = ArrayList<PlayableInfo>()
    var lastTrack: AudioTrackExtender? = null
    var currentTrack: AudioTrackExtender? = null
    private val audioManager: AudioManager

    private var lifecycle = true
    private var ownCurrentTrack: AudioTrackExtender? = null

    init {
        audioPlayer.volume = 50
        audioPlayer.setFrameBufferDuration(5)
        this.audioManager = audioManager
        this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        audioPlayer.addListener(TrackEventListener(this))
        Executors.newCachedThreadPool().submit {
            currentTrackLifecycle()
        }
    }

    private fun currentTrackLifecycle() {
        while (lifecycle) {
            if (ownCurrentTrack != null && ownCurrentTrack != currentTrack) {
                lastTrack = ownCurrentTrack
                println("Last Track: $lastTrack")
            }
            if (audioPlayer.playingTrack == null && currentTrack != null) {
                lastTrack = currentTrack
                currentTrack = null
            }
            ownCurrentTrack = currentTrack
        }
    }

    fun openConnection() {
        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }

    fun closeConnection() {
        audioManager.closeAudioConnection()
    }

    fun changeChannel(newVoiceChannel: VoiceChannel) {
        voiceChannel = newVoiceChannel
    }

    @Throws(TrackNotFoundException::class, GodBotException::class)
    fun playNext(): PlayableInfo {
        if (queue.isEmpty()) {
            throw TrackNotFoundException()
        }
        val playableInfo = queue[0]
        playNow(playableInfo)
        return playableInfo
    }

    fun playNow(playableInfo: PlayableInfo) {
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
        if (audioPlayer.playingTrack == null) {
            playNow(playableInfo)
            return 0
        }
        queue.add(playableInfo)
        return queue.size - 1
    }

    @Throws(IndexOutOfBoundsException::class)
    fun skipTo(index: Int) {
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
        return queue.removeAt(index)
    }

    fun clearQueue() {
        queue = ArrayList()
    }

    fun stop() {
        clearQueue()
        audioPlayer.stopTrack()
    }
}

data class AudioTrackExtender(
    val audioTrack: AudioTrack,
    val playableInfo: PlayableInfo
)
