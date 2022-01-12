package playableInfo

import net.dv8tion.jda.api.entities.Member

class YouTubePlaylist private constructor(
    override val duration: Long,
    override val creator: String,
    override val creatorLink: String?,
    override val title: String,
    override val uri: String?,
    override val thumbnailUri: String?,
    override val requester: Member?,
    override val size: Int,
    override val videoIds: ArrayList<String>,
    override val playableInformation: ArrayList<PlayableInfo>) : PlaylistPlayableInfo {

    data class Builder(
        var duration: Long = 0,
        var creator: String = "Creator",
        var creatorLink: String? = null,
        var title: String = "Playlist",
        var uri: String? = null,
        var thumbnailUri: String? = null,
        var requester: Member? = null,
        var size: Int = 0,
        var videoIds: ArrayList<String> = ArrayList(),
        var playableInformation: ArrayList<PlayableInfo> = ArrayList()) {

        fun duration(duration: Long) = apply { this.duration = duration }
        fun addDuration(duration: Long) = apply { this.duration += duration }
        fun creator(creator: String) = apply { this.creator = creator }
        fun creatorLink(creatorLink: String) = apply { this.creatorLink = creatorLink }
        fun title(title: String) = apply { this.title = title }
        fun uri(uri: String) = apply { this.uri = uri }
        fun thumbnailUri(thumbnailUri: String) = apply { this.thumbnailUri = thumbnailUri }
        fun requester(requester: Member) = apply { this.requester = requester }
        fun size(size: Int) = apply { this.size = size }
        fun videoIds(videoIds: ArrayList<String>) = apply { this.videoIds = videoIds }
        fun addVideoId(videoId: String) = apply { this.videoIds.add(videoId) }
        fun playableInformation(playableInformation: ArrayList<PlayableInfo>) = apply { this.playableInformation = playableInformation }
        fun addPlayable(playable: PlayableInfo) = apply { this.playableInformation.add(playable) }
        fun build() = YouTubePlaylist(duration, creator, creatorLink, title, uri, thumbnailUri, requester, size, videoIds, playableInformation)
    }
}
