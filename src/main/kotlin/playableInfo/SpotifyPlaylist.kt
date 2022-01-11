package playableInfo

import net.dv8tion.jda.api.entities.Member

class SpotifyPlaylist private constructor(
    override val duration: Long,
    override val creator: String,
    override val creatorLink: String?,
    override val title: String,
    override val uri: String?,
    override val thumbnailUri: String?,
    override val requester: Member,
    override val size: Int,
    override val videoIds: ArrayList<String>) : PlaylistPlayableInfo {

    data class Builder(
        var duration: Long,
        var creator: String,
        var creatorLink: String?,
        var title: String,
        var uri: String?,
        var thumbnailUri: String?,
        var requester: Member,
        var size: Int,
        var videoIds: ArrayList<String>) {

        fun duration(duration: Long) = apply { this.duration = duration }
        fun creator(creator: String) = apply { this.creator = creator }
        fun creatorLink(creatorLink: String) = apply { this.creatorLink = creatorLink }
        fun title(title: String) = apply { this.title = title }
        fun uri(uri: String) = apply { this.uri = uri }
        fun thumbnailUri(thumbnailUri: String) = apply { this.thumbnailUri = thumbnailUri }
        fun requester(requester: Member) = apply { this.requester = requester }
        fun size(size: Int) = apply { this.size = size }
        fun videoIds(videoIds: ArrayList<String>) = apply { this.videoIds = videoIds }
        fun addVideoId(videoId: String) = apply { this.videoIds.add(videoId) }
        fun build() = SpotifyPlaylist(duration, creator, creatorLink, title, uri, thumbnailUri, requester, size, videoIds)
    }
}