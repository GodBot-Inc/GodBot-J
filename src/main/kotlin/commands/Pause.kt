package commands

import constants.pauseEmoji
import functions.getPlayingPlayer
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun pause(event: EventWrapper, payload: SlashCommandPayload) {
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
