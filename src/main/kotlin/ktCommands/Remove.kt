package ktCommands

import commands.Command
import ktUtils.getPlayerWithQueue
import objects.AudioTrackExtender
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.ErrorMessages

fun remove(event: EventFacade, payload: SlashCommandPayload) {
    val position: Long? = event.getOption("position")?.asLong
    if (position == null) {
        event.error(ErrorMessages.NOT_RECEIVED_PARAMETER)
        return
    }

    val player = getPlayerWithQueue(
        JDAManager.getInstance().getJDA(Command.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val audioTrack: AudioTrackExtender
    try {
        audioTrack = player.removeTrackAt((position - 1).toInt())
    } catch (e: IndexOutOfBoundsException) {
        event.error("Tracks in Queue: ${player.queue.size}. Given Index: $position")
        return
    }

    event.replyLink("Removed [${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}