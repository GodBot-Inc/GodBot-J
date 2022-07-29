package ktCommands

import constants.cleanedEmoji
import ktUtils.getPlayerWithQueue
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
