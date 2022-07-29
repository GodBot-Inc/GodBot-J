package ktCommands

import constants.leaveEmoji
import ktUtils.getPlayer
import objects.EventFacade
import objects.SlashCommandPayload

fun leave(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.closeConnection()
    event.replyEmote(leaveEmoji, "Left Channel ${payload.voiceChannel.name}")
}
