package ktCommands

import constants.joinEmoji
import lib.lavaplayer.PremiumPlayerManager
import objects.EventFacade
import objects.SlashCommandPayload

fun join(event: EventFacade, payload: SlashCommandPayload) {
    val player = PremiumPlayerManager.getOrCreatePlayer(
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
