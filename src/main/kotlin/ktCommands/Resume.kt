package ktCommands

import commands.Command
import constants.playEmoji
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun resume(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        JDAManager.getInstance().getJDA(Command.applicationId),
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