package ktCommands.queue.features

import features.subscriptions.BotSubscriptions
import ktCommands.queue.utils.compactQueue
import lib.lavaplayer.TrackEvents
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import objects.AudioPlayerExtender
import objects.PlayerEvents

class QueueControllableEmbed(
    private val messageAction: ReplyAction,
    private val player: AudioPlayerExtender,
    private val avatarUrl: String?
    ) {

    var isCompact: Boolean = true
    var pages: Int = player.queue.size
    var page: Int = 1
    private lateinit var message: Message

    init {
        // Send Message
        messageAction.queue { hook ->
            run {
                message = hook.retrieveOriginal().submit().join()
            }
        }
        // Subscribe to update events from the player
        BotSubscriptions.subscribeToPlayerEvents(
            arrayListOf(PlayerEvents.QUEUE, PlayerEvents.CLEANUP),
            ::onPlayerUpdate,
            player
        )
        BotSubscriptions.subscribeToTrackEvent(TrackEvents.START, ::onTrackStart, player)
    }

    // Navigation
    fun firstPage() {

    }

    fun previousPage() {

    }

    fun nextPage() {

    }

    fun lastPage() {

    }


    private fun disable() {
        println("Disable")
    }

    // Updates
    private fun onPlayerUpdate(event: PlayerEvents) {
        if (event == PlayerEvents.CLEANUP) {
            disable()
            return
        }
        // Queue Event
        message.editMessageEmbeds(compactQueue(player.queue, avatarUrl)).queue()
    }

    private fun onTrackStart(event: TrackEvents) {

    }
}
