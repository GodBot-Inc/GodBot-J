package ktCommands

import commands.Command
import lib.AudioTrackExtender
import lib.EventExtender
import lib.SlashCommandPayload
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.ErrorMessages

fun remove(event: EventExtender, payload: SlashCommandPayload) {
    val position: Long? = event.getOption("position")?.asLong
    if (position == null) {
        event.error(ErrorMessages.NOT_RECEIVED_PARAMETER)
        return
    }

    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id
        )
    if (player == null || player.voiceChannel.id != payload.voiceChannel.id) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }
    if (player.queue.isEmpty()) {
        event.error(ErrorMessages.QUEUE_EMPTY)
        return
    }

    val audioTrack: AudioTrackExtender
    try {
        audioTrack = player.removeTrackAt((position - 1).toInt())
    } catch (e: IndexOutOfBoundsException) {
        event.error("Tracks in Queue: ${player.queue.size}. Given Index: $position")
        return
    }

    event.replyLink("Removed [${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}