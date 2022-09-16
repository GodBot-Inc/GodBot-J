package commands

import constants.nextTrackEmoji
import kotlinx.coroutines.runBlocking
import utils.getPlayerWithQueue
import lib.jda.EventFacade
import objects.SlashCommandPayload

fun skip(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val paused = player.isPaused()
    val audioTrack = runBlocking { player.playNext() }
    if (paused)
        player.setPaused(false)

    event.replyEmoteLink(
        nextTrackEmoji, "Skipped Song, Now Playing: " +
            "[${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}