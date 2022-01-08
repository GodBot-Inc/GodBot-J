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
import java.util.concurrent.TimeUnit

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
    private var currentTrackCheck = true

    init {
        audioPlayer.volume = 50
        this.audioManager = audioManager
        if (this.audioManager.sendingHandler == null) {
            this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        }
        audioPlayer.addListener(TrackEventListener(this))
        Executors.newCachedThreadPool().submit {
            currentTrackLifeCycle()
        }
    }

    private fun currentTrackLifeCycle() {
        while (currentTrackCheck) {
            if (audioPlayer.playingTrack == null) {
                lastTrack = currentTrack
                currentTrack = null
            }
            TimeUnit.MILLISECONDS.sleep(500)
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
        lastTrack = currentTrack
        currentTrack = audioTrack
        audioPlayer.playTrack(audioTrack.audioTrack)
    }

    fun play(audioTrack: AudioTrackExtender): Int {
        if (audioPlayer.playingTrack == null) {
            audioPlayer.playTrack(audioTrack.audioTrack)
            lastTrack = currentTrack
            currentTrack = audioTrack
            return 0
        }
        queue.add(audioTrack)
        return queue.size - 1
    }

    fun playNowWithSeek(audioTrack: AudioTrackExtender, thresholdMs: Long) {
        lastTrack = currentTrack
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
