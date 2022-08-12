package commands

import constants.pauseEmoji
import utils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload

fun pause(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    if (player.isPaused()) {
        event.error("Player is already paused")
        return
    }

    player.setPaused(true)
    event.replyEmote(pauseEmoji, "Player paused")
}
