package objects.playableInformation

class SpotifySong private constructor(
    override val duration: Long,
    override val creator: String,
    override val creatorLink: String?,
    override val title: String,
    override val uri: String?,
    override val thumbnailUri: String?,
    val songId: String?) : PlayableInfo {

    data class Builder(
        var duration: Long = 0,
        var creator: String = "Creator",
        var creatorLink: String? = null,
        var title: String = "Song",
        var uri: String? = null,
        var thumbnailUri: String? = null,
        var songId: String? = null) {

        fun duration(duration: Long) = apply { this.duration = duration }
        fun creator(creator: String) = apply { this.creator = creator }
        fun creatorLink(creatorLink: String) = apply { this.creatorLink = creatorLink }
        fun title(title: String) = apply { this.title = title }
        fun uri(uri: String) = apply { this.uri = uri }
        fun thumbnailUri(thumbnailUri: String) = apply { this.thumbnailUri = thumbnailUri }
        fun songId(songId: String) = apply { this.songId = songId }
        fun build() = SpotifySong(duration, creator, creatorLink, title, uri, thumbnailUri, songId)
    }
}