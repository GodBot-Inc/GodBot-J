package lib.jda

import io.github.cdimascio.dotenv.Dotenv
import state.AudioPlayerExtender
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import state.PlayerStorage

class GeneralListener: ListenerAdapter() {
    private val dotenv = Dotenv.load()
    private val applicationId = dotenv["APPLICATIONID"]

    private fun autoPauseMove(event: GuildVoiceMoveEvent, player: AudioPlayerExtender) {
        if (event.channelLeft == player.voiceChannel &&
            event.channelLeft.members.size == 1)
            player.setPaused(true)
        if (event.channelJoined == player.voiceChannel &&
            event.channelJoined.members.size >= 2)
            player.setPaused(false)
    }

    private fun autoPauseJoin(event: GuildVoiceJoinEvent, player: AudioPlayerExtender) {
        if (event.channelJoined == player.voiceChannel &&
            event.channelJoined.members.size >= 2)
            player.setPaused(false)
    }

    private fun autoPauseLeave(event: GuildVoiceLeaveEvent, player: AudioPlayerExtender) {
        if (event.channelLeft == player.voiceChannel &&
            event.channelLeft.members.size == 1)
            player.setPaused(true)
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        val player = PlayerStorage.get(event.guild.id) ?: return
        if (event.member.id == applicationId)
            player.changeChannel(event.channelJoined)

        autoPauseMove(event, player)
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val player = PlayerStorage.get(event.guild.id) ?: return

        autoPauseJoin(event, player)
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val player = PlayerStorage.get(event.guild.id) ?: return

        if (event.member.id == applicationId) {
            player.cleanup()
            return
        }
        autoPauseLeave(event, player)
    }
}
