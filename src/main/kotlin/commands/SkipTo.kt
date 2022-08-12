package commands

import constants.loadingSongFailed
import constants.notReceivedParameter
import kotlinx.coroutines.runBlocking
import utils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload

fun skipTo(event: EventFacade, payload: SlashCommandPayload) {
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

    val paused = player.isPaused()
    try {
        runBlocking { player.skipTo((position - 1).toInt()) }
    } catch (e: IndexOutOfBoundsException) {
        event.error("The Queue is ${player.queue.size} big and the position is $position")
        return
    }
    if (paused)
        player.setPaused(false)

    if (player.currentTrack == null) {
        event.error(loadingSongFailed)
        return
    }

    event.replyLink("Skipped to `$position`, now playing " +
            "[${player.currentTrack!!.songInfo.title}](${player.currentTrack!!.songInfo.uri})")
}