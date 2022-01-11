package playableInfo

import net.dv8tion.jda.api.entities.Member

class YouTubeSong private constructor (
    override val duration: Long,
    override val creator: String,
    override val creatorLink: String?,
    override val title: String,
    override val uri: String?,
    override val thumbnailUri: String?,
    override val requester: Member,
    val songId: String?,
    val likes: Long,
    val views: Long,
    val comments: Long) : PlayableInfo {

        data class Builder(
            var duration: Long = 0,
            var creator: String = "Creator",
            var creatorLink: String? = null,
            var title: String = "Song",
            var uri: String? = null,
            var thumbnailUri: String? = null,
            var requester: Member,
            var songId: String? = null,
            var likes: Long = 0,
            var views: Long = 0,
            var comments: Long = 0) {

            fun duration(duration: Long) = apply { this.duration = duration }
            fun creator(creator: String) = apply { this.creator = creator }
            fun creatorLink(creatorLink: String) = apply { this.creatorLink = creatorLink }
            fun title(title: String) = apply { this.title = title }
            fun uri(uri: String) = apply { this.uri = uri }
            fun thumbnailUri(thumbnailUri: String) = apply { this.thumbnailUri = thumbnailUri }
            fun requester(requester: Member) = apply { this.requester = requester }
            fun songId(songId: String) = apply { this.songId = songId }
            fun likes(likes: Long) = apply { this.likes = likes }
            fun views(views: Long) = apply { this.views = views }
            fun comments(comments: Long) = apply { this.comments = comments }
            fun build() = YouTubeSong(
                duration, creator, creatorLink, title, uri, thumbnailUri, requester, songId, likes, views, comments
            )
        }
}
