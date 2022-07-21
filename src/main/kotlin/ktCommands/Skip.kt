package ktCommands

import constants.nextTrackEmoji
import ktUtils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload

fun skip(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val audioTrack = player.playNext()

    event.replyEmoteLink(
        nextTrackEmoji, "Skipped Song, Now Playing: " +
            "[${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}