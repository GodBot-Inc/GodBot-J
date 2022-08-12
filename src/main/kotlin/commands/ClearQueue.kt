package commands

import constants.cleanedEmoji
import utils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload

fun clearQueue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.clearQueue()

    event.replyEmote(cleanedEmoji, "Removed all tracks from the Queue")
}