package commands

import constants.joinEmoji
import lib.lavaplayer.PlayerManager
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun join(event: EventWrapper, payload: SlashCommandPayload) {
    val player = PlayerManager.getOrCreatePlayer(
        payload.guild,
        payload.voiceChannel
    )
    if (player.isConnected()) {
        event.error("The Player is already connected")
        return
    }

    player.openConnection()
    event.replyEmote(joinEmoji, "Joined the channel ${payload.voiceChannel.name}")
}
