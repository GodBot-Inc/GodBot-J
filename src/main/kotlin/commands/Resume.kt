package commands

import constants.playEmoji
import functions.getPlayingPlayer
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun resume(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    if (!player.isPaused()) {
        event.error("Player is not paused")
        return
    }

    player.setPaused(false)
    event.replyEmote(playEmoji, "Player resumed")
}