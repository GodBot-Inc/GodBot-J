package lib.jda

import functions.QueueButtons
import functions.compactQueue
import functions.getMaxQueuePages
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import state.AudioTrackExtender

class ButtonEventWrapper(private val event: ButtonClickEvent) {

    val messageId = event.messageId
    val buttonId = event.button?.id

    fun updateQueue(queue: ArrayList<AudioTrackExtender>, avatarUrl: String?, page: Int) {
        val buttons = QueueButtons.checkButtons(page, getMaxQueuePages(queue))
        val embed = compactQueue(queue, avatarUrl, page)
        event.editMessageEmbeds(embed).setActionRow(buttons).queue()
    }
}
