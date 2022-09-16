package features

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import lib.lavaplayer.AudioPlayerExtender

fun autoPauseMove(event: GuildVoiceMoveEvent, player: AudioPlayerExtender) {
    if (event.channelLeft == player.voiceChannel &&
        event.channelLeft.members.size == 1)
        player.setPaused(true)
    if (event.channelJoined == player.voiceChannel &&
        event.channelJoined.members.size >= 2)
        player.setPaused(false)
}

fun autoPauseJoin(event: GuildVoiceJoinEvent, player: AudioPlayerExtender) {
    if (event.channelJoined == player.voiceChannel &&
        event.channelJoined.members.size >= 2)
        player.setPaused(false)
}

fun autoPauseLeave(event: GuildVoiceLeaveEvent, player: AudioPlayerExtender) {
    if (event.channelLeft == player.voiceChannel &&
        event.channelLeft.members.size == 1)
        player.setPaused(true)
}
