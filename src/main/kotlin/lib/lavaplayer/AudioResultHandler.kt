package lib.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import ktUtils.LoadFailedException
import ktUtils.TrackNotFoundException

class AudioResultHandler: AudioLoadResultHandler {

    private var audioTrack: AudioTrack? = null

    // 1 -> No Matches
    // 2 -> load Failed
    private var error: Int = 0

    override fun trackLoaded(track: AudioTrack) {
        this.audioTrack = track
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.tracks.isEmpty()) {
            this.error = 1
            return
        }
        this.audioTrack = playlist.tracks[0]
    }
    override fun noMatches() {
        this.error = 1
    }
    override fun loadFailed(exception: FriendlyException?) {
        this.error = 2
    }

    @Throws()
    fun awaitReady(): AudioTrack {
        while (this.error == 0 && audioTrack == null) { }
        if (this.error == 2)
            throw LoadFailedException()
        if (audioTrack != null)
            return audioTrack!!

        throw TrackNotFoundException()
    }
}
