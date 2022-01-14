package playableInfo

import net.dv8tion.jda.api.entities.Member

interface PlayableInfo {
    val duration: Long
    val creator: String
    val creatorLink: String?
    val title: String
    val uri: String?
    val thumbnailUri: String?
    val requester: Member?
}
