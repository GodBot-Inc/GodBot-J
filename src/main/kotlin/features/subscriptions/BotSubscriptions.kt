package features.subscriptions

import lib.lavaplayer.TrackEvents
import objects.AudioPlayerExtender
import objects.PlayerEvents
import state.PlayerStorage

object BotSubscriptions {
    private val playerSubscriptions = ArrayList<(PlayerEvents) -> Unit>()
    private val specificPlayerSubscriptions = HashMap<(PlayerEvents) -> Unit, ArrayList<PlayerEvents>>()

    private val trackSubscriptions = ArrayList<(TrackEvents) -> Unit>()
    private val specificTrackSubscriptions = HashMap<(TrackEvents) -> Unit, ArrayList<TrackEvents>>()

    fun newPlayer(player: AudioPlayerExtender) {
        for (func in playerSubscriptions)
            player.subscribeToPlayerEvents(func)
        for ((func, events) in specificPlayerSubscriptions)
            player.subscribeToPlayerEvents { event ->
                if (events.contains(event))
                    func(event)
            }
        for (func in trackSubscriptions)
            player.subscribeToListenerEvents(func)
        for ((func, events) in specificTrackSubscriptions)
            player.subscribeToListenerEvents { event ->
                if (events.contains(event))
                    func(event)
            }
    }

    // Player Events
    fun subscribeToPlayerEvents(func: (PlayerEvents) -> Unit) {
        PlayerStorage.actionOnEveryPlayer { player ->
            player.subscribeToPlayerEvents(func)
        }
    }

    fun subscribeToPlayerEvents(events: ArrayList<PlayerEvents>, func: (PlayerEvents) -> Unit) {
        PlayerStorage.actionOnEveryPlayer { player ->
            player.subscribeToPlayerEvents { event ->
                    if (events.contains(event))
                        func(event)
            }
        }
    }

    fun subscribeToPlayerEvent(functionEvent: PlayerEvents, func: (PlayerEvents) -> Unit)
    = subscribeToPlayerEvents(arrayListOf(functionEvent), func)


    fun subscribeToPlayerEvents(func: (PlayerEvents) -> Unit, player: AudioPlayerExtender) {
        player.subscribeToPlayerEvents(func)
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

    fun subscribeToPlayerEvent(
        funcEvent: PlayerEvents,
        func: () -> Unit,
        player: AudioPlayerExtender
    ) {
        player.subscribeToPlayerEvents { event ->
            if (event == funcEvent)
                func()
        }
    }


    // Track Events
    fun subscribeToTrackEvents(func: (TrackEvents) -> Unit) {
        PlayerStorage.actionOnEveryPlayer { player ->
            player.subscribeToListenerEvents(func)
        }
    }

    fun subscribeToTrackEvents(events: ArrayList<TrackEvents>, func: (TrackEvents) -> Unit) {
        PlayerStorage.actionOnEveryPlayer { player ->
            player.subscribeToListenerEvents { event ->
                    if (events.contains(event))
                        func(event)
        } }
    }

    fun subscribeToTrackEvent(funcEvent: TrackEvents, func: (TrackEvents) -> Unit)
    = subscribeToTrackEvents(arrayListOf(funcEvent), func)


    fun subscribeToTrackEvents(func: (TrackEvents) -> Unit, player: AudioPlayerExtender) {
        player.subscribeToListenerEvents(func)
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
