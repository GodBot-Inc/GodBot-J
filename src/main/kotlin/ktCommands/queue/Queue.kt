package ktCommands.queue

import ktCommands.queue.features.QueueControllableEmbed
import ktCommands.queue.utils.QueueButtons
import ktCommands.queue.utils.compactQueue
import ktCommands.queue.utils.getMaxQueuePages
import ktUtils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(payload.guild.id, payload.voiceChannel.id, event) ?: return

    val message = event.replyAction(
        compactQueue(player.queue, payload.member.user.avatarUrl),
        QueueButtons.checkButtons(1, getMaxQueuePages(player.queue))
    )
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}
