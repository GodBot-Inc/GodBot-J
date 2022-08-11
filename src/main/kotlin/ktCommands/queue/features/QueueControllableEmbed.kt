package ktCommands.queue.features

import features.ButtonDistributor
import features.subscriptions.BotSubscriptions
import ktCommands.queue.objects.ButtonEventWrapper
import ktCommands.queue.objects.MessageWrapper
import ktCommands.queue.utils.getMaxQueuePages
import lib.lavaplayer.TrackEvents
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import objects.AudioPlayerExtender
import objects.PlayerEvents
import java.util.concurrent.TimeUnit

class QueueControllableEmbed(
    messageAction: ReplyAction,
    private val player: AudioPlayerExtender,
    private val avatarUrl: String?
    ) {

    var page: Int = 1
    private lateinit var message: MessageWrapper

    init {
        // Send Message
        messageAction.queue { hook ->
            run {
                message = MessageWrapper(hook.retrieveOriginal().submit().join())
            }
        }
        // Subscribe to update events from the player
        BotSubscriptions.subscribeToPlayerEvents(
            arrayListOf(PlayerEvents.QUEUE, PlayerEvents.CLEANUP),
            ::onPlayerUpdate,
            player
        )
        BotSubscriptions.subscribeToTrackEvent(TrackEvents.START, ::onTrackStart, player)
        while (!this::message.isInitialized)
            TimeUnit.MILLISECONDS.sleep(10)
        print("Message initialized Subscribing to ButtonDistributor")
        ButtonDistributor.add(message.id, ::resolveButtonAction)
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
    }

    private fun previousPage(event: ButtonEventWrapper) {
        page--
        event.updateQueue(player.queue, avatarUrl, page)
    }

    private fun nextPage(event: ButtonEventWrapper) {
        page++
        event.updateQueue(player.queue, avatarUrl, page)
    }

    private fun lastPage(event: ButtonEventWrapper) {
        page = getMaxQueuePages(player.queue)
        event.updateQueue(player.queue, avatarUrl, page)
    }


    private fun disable() {
        ButtonDistributor.remove(message.id)
        message.disable()
    }

    // Updates
    private fun onPlayerUpdate(event: PlayerEvents) {
        if (event == PlayerEvents.CLEANUP) {
            disable()
            return
        }
        // Queue Event
        message.updateQueue(player.queue, avatarUrl, page)
    }

    private fun onTrackStart(event: TrackEvents) {
        // TODO: Check for track endings
    }
}
