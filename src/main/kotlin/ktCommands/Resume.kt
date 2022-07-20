package ktCommands

import commands.Command
import objects.EventExtender
import objects.SlashCommandPayload
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.EmojiIds
import snippets.ErrorMessages

fun resume(event: EventExtender, payload: SlashCommandPayload) {
    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id,
            payload.voiceChannel.id
        )
    if (player == null) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }
    if (player.queue.isEmpty()) {
        event.error(ErrorMessages.QUEUE_EMPTY)
        return
    }
    if (player.currentTrack == null) {
        event.error(ErrorMessages.NO_PLAYING_TRACK)
        return
    }
    if (!player.isPaused()) {
        event.error("Player is not paused")
        return
    }

    player.setPaused(false)
    event.replyEmote(EmojiIds.play, "Player resumed")
}