package commands

import functions.QueueButtons
import functions.compactQueue
import functions.getMaxQueuePages
import lib.jda.EventFacade
import objects.SlashCommandPayload
import state.QueueControllableEmbed
import utils.getPlayer

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayer(payload.guild.id, payload.voiceChannel.id, event) ?: return

    val message = event.replyAction(
        compactQueue(player.queue, payload.member.user.avatarUrl),
        QueueButtons.checkButtons(1, getMaxQueuePages(player.queue))
    )
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}
