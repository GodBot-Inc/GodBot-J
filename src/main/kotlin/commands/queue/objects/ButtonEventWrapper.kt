package commands.queue.objects

import commands.queue.utils.QueueButtons
import commands.queue.utils.compactQueue
import commands.queue.utils.getMaxQueuePages
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import utils.AudioTrackExtender

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
