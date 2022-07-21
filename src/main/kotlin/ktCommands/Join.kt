package ktCommands

import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager

fun join(event: EventFacade, payload: SlashCommandPayload) {
    val player = AudioPlayerManagerWrapper
        .getInstance()
        .getOrCreatePlayer(
            JDAManager.getInstance().getJDA(payload.applicationId),
            payload.guild.id,
            payload.voiceChannel
        )
    if (player.isConnected()) {
        event.error("The Player is already connected")
        return
    }

    player.openConnection()
    event.reply("Joined the channel ${payload.voiceChannel.name}")
}
