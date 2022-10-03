package commands

import constants.cleanedEmoji
import functions.getPlayerWithQueue
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun clearQueue(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.clearQueue()

    event.replyEmote(cleanedEmoji, "Removed all tracks from the Queue")
}
