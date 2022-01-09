package utils

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavaplayerHandlers.AudioPlayerSendHandler
import lavaplayerHandlers.TrackEventListener
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import playableInfo.PlayableInfo
import java.util.concurrent.Executors

class AudioPlayerExtender(
    val audioPlayer: AudioPlayer,
    var voiceChannel: VoiceChannel,
    audioManager: AudioManager) {

    var loop: Boolean = false
    var shuffle: Boolean = false
    var queue = ArrayList<AudioTrackExtender>()
    var lastTrack: AudioTrackExtender? = null
    var currentTrack: AudioTrackExtender? = null
    private val audioManager: AudioManager

    private var lifecycle = true
    private var ownCurrentTrack: AudioTrackExtender? = null

    init {
        audioPlayer.volume = 50
        audioPlayer.setFrameBufferDuration(349568)
        this.audioManager = audioManager
        this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        audioPlayer.addListener(TrackEventListener(this))
        Executors.newCachedThreadPool().submit {
            lifecycle()
        }
    }

    private fun lifecycle() {
        while (lifecycle) {
            // Check if the playing track was changed
            if (ownCurrentTrack != null && ownCurrentTrack != currentTrack) {
                lastTrack = ownCurrentTrack
                println("Last Track: $lastTrack")
            }
            // Check if no Track is playing
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

    fun playNext(): AudioTrackExtender? {
        if (queue.isEmpty()) {
            return null
        }
        audioPlayer.playTrack(queue[0].audioTrack)

        val cur = queue.removeAt(0)
        currentTrack = cur
        return cur
    }

    fun playNow(audioTrack: AudioTrackExtender) {
        currentTrack = audioTrack
        audioPlayer.stopTrack()
        // playing a clone of the track, to be sure that the same instance of a track is not played twice, since
        // this would throw an IllegalStateException
        audioPlayer.playTrack(audioTrack.audioTrack.makeClone())
    }

    fun play(audioTrack: AudioTrackExtender): Int {
        if (audioPlayer.playingTrack == null) {
            audioPlayer.playTrack(audioTrack.audioTrack)
            currentTrack = audioTrack
            return 0
        }
        queue.add(audioTrack)
        return queue.size - 1
    }

    fun playNowWithSeek(audioTrack: AudioTrackExtender, thresholdMs: Long) {
        currentTrack = audioTrack
        audioTrack.audioTrack.position = thresholdMs
        audioPlayer.playTrack(audioTrack.audioTrack)
    }

    fun addAt(audioTrack: AudioTrackExtender, index: Int) {
        if (queue.size <= index) {
            queue.add(audioTrack)
        } else {
            queue.add(index, audioTrack)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun skipTo(index: Int) {
        if (index < queue.size) {
            currentTrack = queue[index]
            for (i in 0 until index + 1) {
                queue.removeAt(0)
            }
            audioPlayer.playTrack(queue[0].audioTrack)
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun removeTrackAt(index: Int): AudioTrackExtender {
        return queue.removeAt(index)
    }

    fun stop() {
        clearQueue()
        audioPlayer.stopTrack()
    }

    fun clearQueue() {
        queue = ArrayList()
    }

}

data class AudioTrackExtender(
    val audioTrack: AudioTrack,
    val interpretations: HashMap<String, PlayableInfo> = HashMap(),
    val requester: Member
)

data class eventPayload(
    val eventType: Int,
    val payload: Unit
)
