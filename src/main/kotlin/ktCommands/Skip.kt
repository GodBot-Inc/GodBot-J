package ktCommands

import constants.nextTrackEmoji
import ktUtils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun skip(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        JDAManager.getInstance().getJDA(payload.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val audioTrack = player.playNext()

    event.replyEmoteLink(
        nextTrackEmoji, "Skipped Song, Now Playing: " +
            "[${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}