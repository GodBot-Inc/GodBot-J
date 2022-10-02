package state

import lib.lavaplayer.TrackEvents
import utils.PlayerEvents

object BotSubscriptions {
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

    fun subscribeToPlayer(
        func: (PlayerEvents) -> Unit,
        player: AudioPlayerExtender
    ) {
        player.subscribeToPlayerEvents(func)
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
