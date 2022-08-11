package features.subscriptions

import lib.lavaplayer.TrackEvents
import objects.AudioPlayerExtender
import objects.PlayerEvents

object BotSubscriptions {
    //TODO: Add Remove function
    private val specificPlayerSubscriptions = HashMap<(PlayerEvents) -> Unit, ArrayList<PlayerEvents>>()
    private val specificTrackSubscriptions = HashMap<(TrackEvents) -> Unit, ArrayList<TrackEvents>>()

    fun newPlayer(player: AudioPlayerExtender) {
        for ((func, events) in specificPlayerSubscriptions)
            player.subscribeToPlayerEvents { event ->
                if (events.contains(event))
                    func(event)
            }
        for ((func, events) in specificTrackSubscriptions)
            player.subscribeToListenerEvents { event ->
                if (events.contains(event))
                    func(event)
            }
    }

    fun subscribeToPlayerEvents(
        events: ArrayList<PlayerEvents>,
        func: (PlayerEvents) -> Unit,
        player: AudioPlayerExtender
    ) {
        player.subscribeToPlayerEvents { event -> run {
            println("Player Event running in Subscription Lambda ${event.name}")
            if (events.contains(event)) {
                println("Events contains event: ${event.name}")
                func(event)
            }
        } }
    }

    fun subscribeToTrackEvents(
        events: ArrayList<TrackEvents>,
        func: (TrackEvents) -> Unit,
        player: AudioPlayerExtender,
    ) {
        player.subscribeToListenerEvents { event ->
            if (events.contains(event))
                func(event)
        }
    }

    fun subscribeToTrackEvent(
        funcEvent: TrackEvents,
        func: (TrackEvents) -> Unit,
        player: AudioPlayerExtender
    ) = subscribeToTrackEvents(arrayListOf(funcEvent), func, player)
}
