package interactions

import commands.Queue
import ktUtils.ButtonException
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction
import org.bson.Document
import snippets.Buttons
import utils.MongoCommunication

// All the methods are static, so a class would make no sense
const val defaultUrl = "https://cdn.discordapp.com/avatars/424595770214449172/aaf1efe43de45b77b104f42941a71d24" +
        ".webp?size=80"

fun dynamicButtonUpdate(currentPage: Int, pages: Int): List<Button> {
    //NOTE: currentPage is an index and pages is a counter, so pages has to be subtracted with 1,
    // so the method works properly
    val queueBuilder: Buttons.QueueBuilder = Buttons.QueueBuilder()

    if (currentPage == 0) {
        queueBuilder
            .setFirstDisabled(true)
            .setLeftDisabled(true)
    } else {
        queueBuilder.setFirstDisabled(false)
        queueBuilder.setLeftDisabled(false)
    }
    if (currentPage == pages) {
        queueBuilder
            .setRightDisabled(true)
            .setLastDisabled(true)
    } else {
        queueBuilder
            .setRightDisabled(false)
            .setLastDisabled(false)
    }

    return queueBuilder.build().asList()
}

@Throws(ButtonException::class)
fun onQueueFirst(event: ButtonClickEvent) {
    val mongo = MongoCommunication.getInstance()
    val queue: Document = mongo.getQueue(event.messageId) ?: throw ButtonException()
    val pages: Int = (queue["pages"] ?: throw ButtonException()) as Int
    val pageDoc: Document = (queue["pagesDocument"] ?: throw ButtonException()) as Document
    val description: String = (pageDoc["0"]
        ?: throw ButtonException()) as String

    val edit = event.editMessageEmbeds(
        Queue.getQueueEmbed(
            description,
            event.jda.getUserById(
                    queue["authorId"] as String
            )?.avatarUrl ?: defaultUrl,
            0,
            pages
        )
    )

    edit.setActionRow(
        dynamicButtonUpdate(
            0,
            pages - 1
        )
    ).queue()

    mongo.setCurrentQueuePage(event.messageId, 0)
}

@Throws(ButtonException::class)
fun onQueueLeft(event: ButtonClickEvent) {
    val mongo: MongoCommunication = MongoCommunication.getInstance()
    val queue: Document = mongo.getQueue(event.messageId) ?: throw ButtonException()
    val currentPage: Int = (queue["currentPage"] ?: throw ButtonException()) as Int
    val pages: Int = (queue["pages"] ?: throw ButtonException()) as Int
    val pageDoc: Document = (queue["pagesDocument"] ?: throw ButtonException()) as Document
    val description: String = (pageDoc[(currentPage - 1).toString()]
        ?: throw ButtonException()) as String

    val edit : UpdateInteractionAction = event.editMessageEmbeds(
        Queue.getQueueEmbed(
            description,
            event.jda.getUserById(
                queue["authorId"] as String
            )?.avatarUrl ?: defaultUrl,
            currentPage - 1,
            pages
        )
    )

    edit.setActionRow(
        dynamicButtonUpdate(
            currentPage - 1,
            pages - 1
        )
    ).queue()

    mongo.setCurrentQueuePage(event.messageId, currentPage - 1)
}

@Throws(ButtonException::class)
fun onQueueRight(event: ButtonClickEvent) {
    val mongo: MongoCommunication = MongoCommunication.getInstance()
    val queue: Document = mongo.getQueue(event.messageId) ?: throw ButtonException()
    val currentPage: Int = (queue["currentPage"] ?: throw ButtonException()) as Int
    val pages: Int = (queue["pages"] ?: throw ButtonException()) as Int
    val pageDoc: Document = (queue["pagesDocument"] ?: throw ButtonException()) as Document
    val description: String = (pageDoc[(currentPage + 1).toString()]
        ?: throw ButtonException()) as String

    val edit: UpdateInteractionAction = event.editMessageEmbeds(
        Queue.getQueueEmbed(
            description,
            event.jda.getUserById(
                queue["authorId"] as String
            )?.avatarUrl ?: defaultUrl,
            currentPage + 1,
            pages
        )
    )

    edit.setActionRow(
        dynamicButtonUpdate(
            currentPage + 1,
            pages - 1
        )
    ).queue()

    mongo.setCurrentQueuePage(event.messageId, currentPage + 1)
}

@Throws(ButtonException::class)
fun onQueueLast(event: ButtonClickEvent) {
    val mongo: MongoCommunication = MongoCommunication.getInstance()
    val queue: Document = mongo.getQueue(event.messageId) ?: throw ButtonException()
    val pages: Int = (queue["pages"] ?: throw ButtonException()) as Int
    val pageDoc: Document = (queue["pagesDocument"] ?: throw ButtonException()) as Document
    val description: String = (pageDoc[(pages - 1).toString()]
        ?: throw ButtonException()) as String

    val edit: UpdateInteractionAction = event.editMessageEmbeds(
        Queue.getQueueEmbed(
            description,
            event.jda.getUserById(
                queue["authorId"] as String
            )?.avatarUrl ?: defaultUrl,
            pages - 1,
            pages
        )
    )

    edit.setActionRow(
        dynamicButtonUpdate(
            pages - 1,
            pages - 1
        )
    ).queue()

    mongo.setCurrentQueuePage(event.messageId, pages - 1)
}
