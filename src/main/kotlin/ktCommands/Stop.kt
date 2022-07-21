package ktCommands

import constants.stopEmoji
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun stop(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        JDAManager.getInstance().getJDA(payload.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.stop()
    event.replyEmote(stopEmoji, "Player stopped")
}
