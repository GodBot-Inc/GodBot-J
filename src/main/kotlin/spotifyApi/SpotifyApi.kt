package spotifyApi

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.runBlockingOnJvmAndNative
import io.github.cdimascio.dotenv.Dotenv
import ktUtils.CredentialsNotFound
import ktUtils.PlaylistNotFoundException
import ktUtils.TrackNotFoundException
import ktUtils.VideoNotFoundException
import net.dv8tion.jda.api.entities.Member
import playableInfo.PlayableInfo
import playableInfo.SpotifyPlaylist
import playableInfo.SpotifySong

private val dotenv: Dotenv = Dotenv.load()
private val clientId: String? = dotenv["SPOT_CLIENT_ID"]
private val clientSecret: String? = dotenv["SPOT_CLIENT_SECRET"]
private var api: SpotifyAppApi? = null


@Throws(CredentialsNotFound::class)
fun initialize() {
    if (clientId == null) {
        throw CredentialsNotFound()
    } else if (clientSecret == null) {
        throw CredentialsNotFound()
    }
    runBlockingOnJvmAndNative {
        api = spotifyAppApi(clientId, clientSecret).build()
    }
}

@Throws(TrackNotFoundException::class)
fun getSongInfo(id: String, requester: Member): SpotifySong {
    if (api == null) {
        initialize()
    }

    val track: Track = runBlockingOnJvmAndNative {
        api!!.tracks.getTrack(id) ?: throw VideoNotFoundException()
    }

    return trackToPlayableInfo(track, requester)
}

private fun trackToPlayableInfo(track: Track, requester: Member): SpotifySong {
    return SpotifySong.Builder()
        .duration(track.length.toLong())
        .creator(track.artists[0].name)
        .creatorLink(track.artists[0].href)
        .title(track.name)
        .uri(track.href)
        .songId(track.id)
        .requester(requester)
        .build()
}

@Throws(PlaylistNotFoundException::class)
fun getPlaylistInfo(id: String, requester: Member): SpotifyPlaylist {
    if (api == null) {
        initialize()
    }

    val playlist: Playlist = runBlockingOnJvmAndNative {
        api!!.playlists.getPlaylist(id) ?: throw PlaylistNotFoundException()
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
        playableInformation.add(trackToPlayableInfo(track.track?.asTrack ?: continue, requester))
    }

    val builder = SpotifyPlaylist.Builder()
        .duration(duration)
        .creatorLink(playlist.owner.href)
        .title(playlist.name)
        .uri(playlist.uri.uri)
        .thumbnailUri(playlist.images[0].url)
        .size(playlist.tracks.size)
        .videoIds(videoIds)
        .playableInfo(playableInformation)

    val creator: String? = playlist.owner.displayName
    if (creator != null) {
        builder.creator = creator
    }

    return builder.build()
}
