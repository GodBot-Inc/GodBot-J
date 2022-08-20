package commands.queue.objects

import commands.queue.utils.QueueButtons
import commands.queue.utils.compactQueue
import commands.queue.utils.getMaxQueuePages
import net.dv8tion.jda.api.entities.Message
import utils.AudioTrackExtender

class MessageWrapper(private val message: Message) {

    val id = message.id

    fun updateQueue(
        queue: ArrayList<AudioTrackExtender>,
        avatarUrl: String?,
        page: Int
    ) {
        val buttons = QueueButtons.checkButtons(page, getMaxQueuePages(queue))
        val embed = compactQueue(queue, avatarUrl, page)
        if (buttons == null)
            message.editMessageEmbeds(embed).queue()
        else
            message
                .editMessageEmbeds(embed)
                .setActionRow(buttons)
                .queue()
    }

    fun disable() {
        message
            .editMessageEmbeds(message.embeds)
            .setActionRow(QueueButtons.allDisabled())
            .queue()
    }
}
