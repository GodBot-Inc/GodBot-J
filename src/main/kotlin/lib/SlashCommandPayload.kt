package lib

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel

data class SlashCommandPayload(
    val voiceChannel: VoiceChannel,
    val guild: Guild,
    val member: Member,
    val applicationId: String,
)
