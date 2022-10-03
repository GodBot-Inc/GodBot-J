package state

import functions.getMaxQueuePages
import lib.jda.ButtonEventWrapper
import lib.jda.MessageWrapper
import lib.lavaplayer.TrackEvents
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import utils.PlayerEvents
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class QueueControllableEmbed(
    messageAction: ReplyAction,
    private val player: AudioPlayerExtender,
    private val avatarUrl: String?
    ) {

    private var page: Int = 1
    private lateinit var message: MessageWrapper
    private var maxQueuePages: Int = getMaxQueuePages(player.queue)

    private var lifecycle = true
    private var lastAction = System.currentTimeMillis()

    init {
        // Send Message
        messageAction.queue { hook ->
            run {
                message = MessageWrapper(hook.retrieveOriginal().submit().join())
            }
        }
        BotSubscriptions.subscribeToPlayer(
            ::onPlayerUpdate,
            player
        )
        BotSubscriptions.subscribeToTrackEvent(TrackEvents.START, ::onTrackStart, player)
        while (!this::message.isInitialized)
            TimeUnit.MILLISECONDS.sleep(10)
        println("Message initialized Subscribing to ButtonDistributor")
        ButtonDistributor.add(message.id, ::resolveButtonAction)
        thread { lifecycle() }
    }

    private fun lifecycle() {
        while (lifecycle) {
            if (System.currentTimeMillis() - lastAction >= TimeUnit.MINUTES.toMillis(10))
                disable()
            TimeUnit.SECONDS.sleep(5)
        }
    }

    private fun update() {
        maxQueuePages = getMaxQueuePages(player.queue)
        lastAction = System.currentTimeMillis()
    }

    private fun resolveButtonAction(event: ButtonEventWrapper) {
        when (event.buttonId) {
            "first" -> firstPage(event)
            "left" -> previousPage(event)
            "right" -> nextPage(event)
            "last" -> lastPage(event)
        }
    }

    // Navigation
    private fun firstPage(event: ButtonEventWrapper) {
        page = 1
        event.updateQueue(player.queue, avatarUrl, page)
        update()
    }

    private fun previousPage(event: ButtonEventWrapper) {
        page--
        event.updateQueue(player.queue, avatarUrl, page)
        update()
    }

    private fun nextPage(event: ButtonEventWrapper) {
        page++
        event.updateQueue(player.queue, avatarUrl, page)
        update()
    }

    private fun lastPage(event: ButtonEventWrapper) {
        page = getMaxQueuePages(player.queue)
        event.updateQueue(player.queue, avatarUrl, page)
        update()
    }

    private fun disable() {
        ButtonDistributor.remove(message.id)
        if (maxQueuePages != 1)
            message.disable()
        lifecycle = false
    }

    // Updates
    private fun onPlayerUpdate(event: PlayerEvents) {
        if (event == PlayerEvents.CLEANUP) {
            disable()
            return
        }
        // Queue Event
        message.updateQueue(player.queue, avatarUrl, page)
        maxQueuePages = getMaxQueuePages(player.queue)
    }

    private fun onTrackStart(event: TrackEvents) {
        if (event == TrackEvents.START)
            message.updateQueue(player.queue, avatarUrl, page)
        maxQueuePages = getMaxQueuePages(player.queue)
    }
}
