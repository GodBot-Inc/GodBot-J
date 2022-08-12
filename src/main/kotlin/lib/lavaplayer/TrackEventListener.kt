package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import kotlinx.coroutines.runBlocking
import utils.QueueEmptyException
import objects.AudioPlayerExtender

class TrackEventListener(val audioPlayer: AudioPlayerExtender): AudioEventAdapter() {

    private val trackEventSubscribers: ArrayList<(TrackEvents) -> Unit> = ArrayList()

    fun subscribe(func: (TrackEvents) -> Unit) = trackEventSubscribers.add(func)

    override fun onPlayerPause(player: AudioPlayer) {
        super.onPlayerPause(player)
    }

    override fun onPlayerResume(player: AudioPlayer) {
        super.onPlayerResume(player)
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        if (trackEventSubscribers.isNotEmpty())
            trackEventSubscribers.forEach { func -> func(TrackEvents.START) }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        println("Track Ended: ${track.info.title} Reason: $endReason")
        if (audioPlayer.loop && endReason != AudioTrackEndReason.STOPPED && audioPlayer.currentTrack != null) {
            runBlocking { audioPlayer.playNowOrNext(audioPlayer.currentTrack!!) }
            return
        }
        if (endReason.mayStartNext) {
            try {
                runBlocking { audioPlayer.playNext() }
            } catch (ignore: QueueEmptyException) { }
        }
        if (trackEventSubscribers.isNotEmpty())
            trackEventSubscribers.forEach { func -> func(TrackEvents.END) }
    }

    override fun onTrackException(player: AudioPlayer, track: AudioTrack, exception: FriendlyException) {
        super.onTrackException(player, track, exception)
    }
}
