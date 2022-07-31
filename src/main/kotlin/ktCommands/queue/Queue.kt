package ktCommands.queue

import ktCommands.queue.features.QueueControllableEmbed
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(payload.guild.id, payload.voiceChannel.id, event) ?: return
    val message = event.reply("Queued x0")
    QueueControllableEmbed(message, player)
}
