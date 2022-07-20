package ktCommands

import commands.Command
import objects.SlashCommandPayload
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.ErrorMessages
import objects.EventExtender
import snippets.EmojiIds

fun pause(event: EventExtender, payload: SlashCommandPayload) {
    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id
        )
    if (player == null || player.voiceChannel.id !== payload.voiceChannel.id) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }
    if (player.currentTrack == null) {
        event.error(ErrorMessages.NO_PLAYING_TRACK)
        return
    }
    if (player.isPaused()) {
        event.error("Player is already paused")
        return
    }

    player.setPaused(true)
    event.replyEmote(EmojiIds.pause, "Player paused")
}
