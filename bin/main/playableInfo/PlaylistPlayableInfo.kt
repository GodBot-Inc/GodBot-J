package playableInfo

interface PlaylistPlayableInfo : PlayableInfo {
    val size: Int
    val videoIds: ArrayList<String>
}