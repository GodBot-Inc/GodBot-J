package commands

import constants.leaveEmoji
import functions.getPlayer
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun leave(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.closeConnection()
    event.replyEmote(leaveEmoji, "Left Channel ${payload.voiceChannel.name}")
}
