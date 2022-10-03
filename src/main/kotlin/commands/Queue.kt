package commands

import functions.QueueButtons
import functions.compactQueue
import functions.getMaxQueuePages
import lib.jda.EventWrapper
import objects.SlashCommandPayload
import state.QueueControllableEmbed
import functions.getPlayer

fun queue(event: EventWrapper, payload: SlashCommandPayload) {
    val player = getPlayer(payload.guild.id, payload.voiceChannel.id, event) ?: return

    val message = event.replyAction(
        compactQueue(player.queue, payload.member.user.avatarUrl),
        QueueButtons.checkButtons(1, getMaxQueuePages(player.queue))
    )
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}
