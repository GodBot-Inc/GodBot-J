package objects.playableInformation

class YouTubePlaylist private constructor(
    override val duration: Long,
    override val creator: String,
    override val creatorLink: String?,
    override val title: String,
    override val uri: String?,
    override val thumbnailUri: String?,
    override val size: Int,
    override val videoIds: ArrayList<String>,
    override val songInfos: ArrayList<PlayableInfo>) : PlaylistPlayableInfo {

    data class Builder(
        var duration: Long = 0,
        var creator: String = "Creator",
        var creatorLink: String? = null,
        var title: String = "Playlist",
        var uri: String? = null,
        var thumbnailUri: String? = null,
        var size: Int = 0,
        var videoIds: ArrayList<String> = ArrayList(),
        var songInfos: ArrayList<PlayableInfo> = ArrayList())
    {

        fun duration(duration: Long) = apply { this.duration = duration }
        fun addDuration(duration: Long) = apply { this.duration += duration }
        fun creator(creator: String) = apply { this.creator = creator }
        fun creatorLink(creatorLink: String) = apply { this.creatorLink = creatorLink }
        fun title(title: String) = apply { this.title = title }
        fun uri(uri: String) = apply { this.uri = uri }
        fun thumbnailUri(thumbnailUri: String) = apply { this.thumbnailUri = thumbnailUri }
        fun size(size: Int) = apply { this.size = size }
        fun addVideoId(videoId: String) = apply { this.videoIds.add(videoId) }
        fun addSongInfo(songInfo: PlayableInfo) = apply { this.songInfos.add(songInfo) }

        fun build() = YouTubePlaylist(duration, creator, creatorLink, title, uri, thumbnailUri, size, videoIds, songInfos)
    }
}
