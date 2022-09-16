package commands

import constants.playEmoji
import utils.getPlayingPlayer
import lib.jda.EventFacade
import objects.SlashCommandPayload

fun resume(event: EventFacade, payload: SlashCommandPayload) {
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