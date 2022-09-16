package commands

import constants.stopEmoji
import utils.getPlayingPlayer
import lib.jda.EventFacade
import objects.SlashCommandPayload

fun stop(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.stop()
    event.replyEmote(stopEmoji, "Player stopped")
}
