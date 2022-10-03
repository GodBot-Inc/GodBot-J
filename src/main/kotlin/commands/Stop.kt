package commands

import constants.stopEmoji
import functions.getPlayingPlayer
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun stop(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.stop()
    event.replyEmote(stopEmoji, "Player stopped")
}
