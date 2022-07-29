package objects.playableInformation

interface PlaylistPlayableInfo : PlayableInfo {
    val size: Int
    val videoIds: ArrayList<String>
    val songInfos: ArrayList<PlayableInfo>
}
