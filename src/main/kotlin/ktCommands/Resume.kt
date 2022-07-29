package ktCommands

import constants.playEmoji
import ktUtils.getPlayingPlayer
import objects.EventFacade
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