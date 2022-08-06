package ktCommands.queue

import ktCommands.queue.features.QueueControllableEmbed
import ktCommands.queue.utils.compactQueue
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import kotlin.math.ceil

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(payload.guild.id, payload.voiceChannel.id, event) ?: return
    val maxPages = ceil(player.queue.size.toDouble() / 10)
    val message = event.replyAction(compactQueue(player.queue, payload.member.user.avatarUrl, maxPages.toInt()))
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}
