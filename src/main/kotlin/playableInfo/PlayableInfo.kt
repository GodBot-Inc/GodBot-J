package playableInfo

interface PlayableInfo {
    val duration: Long
    val creator: String
    val creatorLink: String?
    val title: String
    val uri: String?
    val thumbnailUri: String?
}