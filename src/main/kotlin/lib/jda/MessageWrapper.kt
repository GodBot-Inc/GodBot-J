package lib.jda

import functions.QueueButtons
import functions.compactQueue
import functions.getMaxQueuePages
import net.dv8tion.jda.api.entities.Message
import state.AudioTrackExtender

class MessageWrapper(private val message: Message) {

    val id = message.id

    fun updateQueue(
        queue: ArrayList<AudioTrackExtender>,
        avatarUrl: String?,
        page: Int
    ) {
        val buttons = QueueButtons.checkButtons(page, getMaxQueuePages(queue))
        val embed = compactQueue(queue, avatarUrl, page)
        message.editMessageEmbeds(embed).setActionRow(buttons).queue()
    }

    fun disable() {
        message.editMessageEmbeds(message.embeds).setActionRow(QueueButtons.allDisabled()).queue()
    }
}
