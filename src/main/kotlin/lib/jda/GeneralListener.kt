package lib.jda

import features.autoPauseJoin
import features.autoPauseLeave
import features.autoPauseMove
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import state.PlayerStorage

class GeneralListener: ListenerAdapter() {
    private val dotenv = Dotenv.load()
    private val applicationId = dotenv["APPLICATIONID"]

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
