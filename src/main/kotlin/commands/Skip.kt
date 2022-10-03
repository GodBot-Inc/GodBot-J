package commands

import constants.nextTrackEmoji
import kotlinx.coroutines.runBlocking
import functions.getPlayerWithQueue
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun skip(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val paused = player.isPaused()
    val audioTrack = runBlocking { player.playNextTrack() }
    if (paused)
        player.setPaused(false)

    event.replyEmoteLink(
        nextTrackEmoji, "Skipped Song, Now Playing: " +
            "[${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}