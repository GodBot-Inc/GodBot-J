package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import utils.LoadFailedException
import utils.TrackNotFoundException

class AudioResultHandler: AudioLoadResultHandler {

    var audioTrack: AudioTrack? = null

    // 1 -> No Matches
    // 2 -> load Failed
    var error: Int = 0

    override fun trackLoaded(track: AudioTrack) {
        audioTrack = track
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.tracks.isEmpty()) {
            error = 1
            return
        }
        audioTrack = playlist.tracks[0]
    }
    override fun noMatches() {
        error = 1
    }
    override fun loadFailed(exception: FriendlyException?) {
        error = 2
    }
}

@Throws(LoadFailedException::class, TrackNotFoundException::class)
suspend fun awaitReady(resultHandler: AudioResultHandler) = coroutineScope {
    while (resultHandler.error == 0 && resultHandler.audioTrack == null) {
        try {
            withContext(Dispatchers.IO) {
                Thread.sleep(100)
            }
        } catch (ignore: InterruptedException) {}
    }
    if (resultHandler.error == 2)
        throw LoadFailedException()
    if (resultHandler.audioTrack != null)
        return@coroutineScope resultHandler.audioTrack!!

    throw TrackNotFoundException()
}
