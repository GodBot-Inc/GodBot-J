package ktCommands

import commands.Command
import ktUtils.getPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun leave(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayer(
        JDAManager.getInstance().getJDA(Command.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.closeConnection()
    event.reply("Left Channel ${payload.voiceChannel.name}")
}
