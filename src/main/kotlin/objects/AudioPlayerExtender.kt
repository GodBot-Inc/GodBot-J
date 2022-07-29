package objects

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.runBlocking
import ktUtils.GodBotException
import ktUtils.LoadFailedException
import ktUtils.QueueEmptyException
import ktUtils.TrackNotFoundException
import lib.lavaplayer.*
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.managers.AudioManager
import objects.playableInformation.PlayableInfo
import state.PlayerStorage
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class AudioPlayerExtender(
    private val audioPlayer: AudioPlayer,
    var voiceChannel: VoiceChannel,
    audioManager: AudioManager) {

    var loop = false
    val queue = ArrayList<AudioTrackExtender>()
    var currentTrack: AudioTrackExtender? = null
    private val audioManager: AudioManager

    private var lifecycle = true
    private var lastAction = System.currentTimeMillis()

    init {
        audioPlayer.volume = 50
        audioPlayer.setFrameBufferDuration(200)
        this.audioManager = audioManager
        this.audioManager.sendingHandler = AudioPlayerSendHandler(audioPlayer)
        audioPlayer.addListener(TrackEventListener(this))
        // Execute both tasks async
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

    fun seek(seekPoint: Long) = apply { audioPlayer.playingTrack.position = seekPoint;updateUsage() }

    fun cleanup() {
        audioManager.closeAudioConnection()
        queue.clear()
        currentTrack = null
        lastAction = 0
        lifecycle = false
        // only stops the player
        audioPlayer.destroy()
        PlayerStorage.remove(this.audioManager.guild.id)
    }

    fun openConnection() = apply { updateUsage(); if(!audioManager.isConnected) audioManager.openAudioConnection(voiceChannel) }

    fun closeConnection() = apply { audioManager.closeAudioConnection(); cleanup() }

    fun changeChannel(newVoiceChannel: VoiceChannel) = apply { updateUsage(); voiceChannel = newVoiceChannel }

    suspend fun playNowOrNext(audioTrackExtender: AudioTrackExtender) {
        updateUsage()
        try {
            // Play Now already sets current Track
            playNow(audioTrackExtender)
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
    suspend fun playNext(): AudioTrackExtender {
        updateUsage()
        if (queue.isEmpty()) {
            currentTrack = null
            throw QueueEmptyException()
        }
        val audioTrack = queue.removeAt(0)
        try {
            // Play now already sets currentTrack
            playNow(audioTrack)
        } catch (e: GodBotException) {
            // Recursion, so eventually the method will end
            return playNext()
        }
        // In case of expected method process
        currentTrack = audioTrack
        return audioTrack
    }

    @Throws(TrackNotFoundException::class, LoadFailedException::class)
    suspend fun playNow(audioTrackExtender: AudioTrackExtender) {
        updateUsage()
        val callback = AudioResultHandler()
        val url = audioTrackExtender.songInfo.uri ?: throw TrackNotFoundException()
        PremiumPlayerManager.loadItem(
            url,
            callback
        )

        val playableTrack: AudioTrack = runBlocking { awaitReady(callback) }
        audioPlayer.stopTrack()
        currentTrack = audioTrackExtender
        audioPlayer.playTrack(playableTrack)
    }

    @Throws(TrackNotFoundException::class, LoadFailedException::class)
    suspend fun play(playableInfo: PlayableInfo, payload: SlashCommandPayload): Int {
        return this.play(AudioTrackExtender(playableInfo, payload.member))
    }

    @Throws(TrackNotFoundException::class)
    suspend fun play(audioTrackExtender: AudioTrackExtender): Int {
        updateUsage()
        if (!audioManager.isConnected)
            audioManager.openAudioConnection(voiceChannel)

        if (audioPlayer.playingTrack == null && queue.size == 0) {
            // playNow sets currentTrack
            playNow(audioTrackExtender)
            return 0
        }
        queue.add(audioTrackExtender)
        return queue.size - 1
    }

    @Throws(IndexOutOfBoundsException::class)
    suspend fun skipTo(index: Int) {
        updateUsage()
        if (index < queue.size) {
            val playableInfo = queue[index]
            for (i in 0 until index + 1)
                queue.removeAt(0)
            // playNowOrNext sets currentTrack
            playNowOrNext(playableInfo)
        } else
            throw IndexOutOfBoundsException()
    }

    @Throws(IndexOutOfBoundsException::class)
    fun removeTrackAt(index: Int): AudioTrackExtender {
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

    fun getCurrentSongDuration() = audioPlayer.playingTrack?.duration ?: 0

    fun isConnected() = audioManager.isConnected

    fun clearQueue() = apply { updateUsage(); queue.clear() }

    fun stop() = apply { updateUsage(); clearQueue(); audioPlayer.stopTrack() }
}

data class AudioTrackExtender(val songInfo: PlayableInfo, val requester: Member)