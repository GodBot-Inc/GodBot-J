package ktCommands

import ktUtils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.EmojiIds

fun skip(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        JDAManager.getInstance().getJDA(payload.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val audioTrack = player.playNext()

    event.replyEmoteLink(EmojiIds.nextTrack, "Skipped Song, Now Playing: " +
            "[${audioTrack.songInfo.title}](${audioTrack.songInfo.uri})")
}