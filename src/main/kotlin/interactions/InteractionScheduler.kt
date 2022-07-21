package interactions

import GodBotJda
import ktUtils.ButtonException
import ktUtils.MessageNotFoundException
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.components.Button
import utils.MongoCommunication
import java.util.concurrent.TimeUnit

class InteractionScheduler(
    private val textChannelId: String,
    private val messageId: String,
    private var time: Int,
    private val expiredButtons: List<Button>
    ) {
    private val jda: JDA = GodBotJda!!

    init {
        // Conversion to milliseconds
        time *= 60000
    }

    private fun updateMessage() : Message {
        return (jda.getTextChannelById(textChannelId) ?: throw MessageNotFoundException())
            .retrieveMessageById(messageId).submit().join()
    }

    // This should be called in another thread pool to prevent the main thread from being stuck for the timer duration
    /*
    Java:
        Executors.newCachedThreadPool().submit(() -> funcCall(
            args
        )
        or
        Executors.newCachedThreadPool().submit(() -> {
            code
        })

     Kotlin:
        Executors.newCachedThreadPool().submit {
            code
        }
     */
    @Throws(ButtonException::class)
    fun start(): InteractionScheduler {
        var message: Message
        val start: Long = System.currentTimeMillis()

        while (System.currentTimeMillis() - start < time) {
            message = updateMessage()
            if (message.buttons.isEmpty()) {
                throw ButtonException()
            }
            TimeUnit.SECONDS.sleep(10)
        }

        message = updateMessage()
        message.editMessageEmbeds(
            message.embeds
        ).setActionRow(
            expiredButtons
        ).queue()

        return this
    }

    fun deleteQueue() {
        MongoCommunication.getInstance().deleteQueue(messageId)
    }
}
