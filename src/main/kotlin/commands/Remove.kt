package commands

import constants.notReceivedParameter
import functions.getPlayerWithQueue
import state.AudioTrackExtender
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun remove(event: EventWrapper, payload: SlashCommandPayload) {
    val position: Long? = event.getLong("position")
    if (position == null) {
        event.error(notReceivedParameter)
        return
    }

    val player = getPlayerWithQueue(
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