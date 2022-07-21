package lib.jda

import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import singeltons.JDAManager
import singeltons.PlayerVault

class GeneralListener: ListenerAdapter() {
    private val dotenv = Dotenv.load()
    private val applicationId = dotenv["APPLICATIONID"]

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        val player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                event.guild.id
            ) ?: return
        if (event.member.id == applicationId)
            player.changeChannel(event.channelJoined)

        if (event.channelLeft == player.voiceChannel &&
            event.channelLeft.members.size == 1)
            player.setPaused(true)

        if (event.channelJoined == player.voiceChannel &&
            event.channelJoined.members.size >= 2)
            player.setPaused(false)
    }

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                event.guild.id
            ) ?: return

        if (event.channelJoined == player.voiceChannel &&
            event.channelJoined.members.size == 2) {
            player.setPaused(true)
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                event.guild.id
            ) ?: return

        if (event.member.id == applicationId) {
            player.cleanup()
            return
        }
        if (event.channelLeft == player.voiceChannel &&
            event.channelLeft.members.size == 1)
            player.setPaused(true)
    }
}
