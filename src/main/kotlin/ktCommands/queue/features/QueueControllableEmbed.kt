package ktCommands.queue.features

import features.subscriptions.BotSubscriptions
import lib.lavaplayer.TrackEvents
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import objects.AudioPlayerExtender
import objects.PlayerEvents

class QueueControllableEmbed(
    private val messageAction: ReplyAction,
    private val player: AudioPlayerExtender
    ) {

    var isCompact: Boolean = true
    var pages: Int = player.queue.size
    var page: Int = 1
    private var queued = 0
    lateinit var message: Message

    init {
        messageAction.queue { hook ->
            run {
                message = hook.retrieveOriginal().submit().join()
            }
        }
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

    // Other Button Actions
    fun disable() {
        println("Disable")
    }

    // Updates
    private fun onPlayerUpdate(event: PlayerEvents) {
        if (event == PlayerEvents.CLEANUP) {
            disable()
            return
        }
        queued += 1
        println("Queued $queued")
        message.editMessage("Queued $queued").queue()
    }

    private fun onTrackStart(event: TrackEvents) {

    }
}
