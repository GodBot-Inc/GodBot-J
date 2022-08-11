package ktCommands.queue.objects

import ktCommands.queue.utils.QueueButtons
import ktCommands.queue.utils.compactQueue
import ktCommands.queue.utils.getMaxQueuePages
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import objects.AudioTrackExtender

class ButtonEventWrapper(private val event: ButtonClickEvent) {

    val messageId = event.messageId
    val buttonId = event.button?.id

    fun updateQueue(queue: ArrayList<AudioTrackExtender>, avatarUrl: String?, page: Int) {
        val buttons = QueueButtons.checkButtons(page, getMaxQueuePages(queue))
        val embed = compactQueue(queue, avatarUrl, page)
        if (buttons == null)
            event.editMessageEmbeds(embed).queue()
        else
            event.editMessageEmbeds(embed).setActionRow(buttons).queue()
    }
}
