package commands.queue

import commands.queue.features.QueueControllableEmbed
import commands.queue.utils.QueueButtons
import commands.queue.utils.compactQueue
import commands.queue.utils.getMaxQueuePages
import utils.getPlayerWithQueue
import lib.jda.EventFacade
import objects.SlashCommandPayload

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(payload.guild.id, payload.voiceChannel.id, event) ?: return

    val message = event.replyAction(
        compactQueue(player.queue, payload.member.user.avatarUrl),
        QueueButtons.checkButtons(1, getMaxQueuePages(player.queue))
    )
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}
