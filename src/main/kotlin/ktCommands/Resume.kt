package ktCommands

import commands.Command
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.EmojiIds

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
    event.replyEmote(EmojiIds.play, "Player resumed")
}