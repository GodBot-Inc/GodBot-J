package ktCommands.queue.objects

import ktCommands.queue.utils.QueueButtons
import ktCommands.queue.utils.compactQueue
import ktCommands.queue.utils.getMaxQueuePages
import net.dv8tion.jda.api.entities.Message
import objects.AudioTrackExtender

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
