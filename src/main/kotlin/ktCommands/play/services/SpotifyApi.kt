package spotifyApi

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.runBlockingOnJvmAndNative
import io.github.cdimascio.dotenv.Dotenv
import ktUtils.PlaylistNotFoundException
import ktUtils.TrackNotFoundException
import ktUtils.VideoNotFoundException
import objects.playableInformation.PlayableInfo
import objects.playableInformation.SpotifyPlaylist
import objects.playableInformation.SpotifySong
import java.util.logging.Logger

private val dotenv: Dotenv = Dotenv.load()
private val clientId: String = dotenv["SPOT_CLIENT_ID"]
private val clientSecret: String = dotenv["SPOT_CLIENT_SECRET"]
val logger: Logger = Logger.getLogger("SpotifyApiLogger")
private var api: SpotifyAppApi = runBlockingOnJvmAndNative {
    spotifyAppApi(clientId, clientSecret).build()
}

@Throws(TrackNotFoundException::class)
fun getSongInfo(id: String): SpotifySong {
    val track: Track = runBlockingOnJvmAndNative {
        api.tracks.getTrack(id) ?: throw VideoNotFoundException()
    }

    return trackToPlayableInfo(track)
}

@Throws(PlaylistNotFoundException::class)
fun getPlaylistInfo(id: String): SpotifyPlaylist {
    val playlist: Playlist = runBlockingOnJvmAndNative {
        api.playlists.getPlaylist(id) ?: throw PlaylistNotFoundException()
    }

    var duration: Long = 0
    val videoIds: ArrayList<String> = ArrayList()
    val playableInformation: ArrayList<PlayableInfo> = ArrayList()
    for (track: PlaylistTrack in playlist.tracks) {
        val trackId: String? = track.track?.id
        if (trackId != null) {
            videoIds.add(trackId)
        }
        duration += track.track?.asTrack?.length ?: 0
        playableInformation.add(trackToPlayableInfo(track.track?.asTrack ?: continue))
    }

    val builder = SpotifyPlaylist.Builder()
        .duration(duration)
        .creatorLink(playlist.owner.href)
        .title(playlist.name)
        .uri(playlist.uri.uri)
        .thumbnailUri(playlist.images[0].url)
        .size(playlist.tracks.size)
        .videoIds(videoIds)

    val creator: String? = playlist.owner.displayName
    if (creator != null) {
        builder.creator = creator
    }

    return builder.build()
}

private fun trackToPlayableInfo(track: Track): SpotifySong {
    return SpotifySong.Builder()
        .duration(track.length.toLong())
        .creator(track.artists[0].name)
        .creatorLink(track.artists[0].href)
        .title(track.name)
        .uri(track.href)
        .songId(track.id)
        .build()
}
