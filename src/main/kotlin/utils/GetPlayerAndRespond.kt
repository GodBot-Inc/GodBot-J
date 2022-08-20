package utils

import constants.noPlayerInVc
import constants.noPlayingTrack
import constants.queueEmpty
import objects.EventFacade
import state.PlayerStorage


fun getPlayer(guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = PlayerStorage.get(guildId)
    return if (player == null || player.voiceChannel.id != channelId) {
        event.error(noPlayerInVc)
        null
    } else {
        player
    }
}

fun getPlayingPlayer(guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayer(guildId, channelId, event) ?: return null

    return if (player.currentTrack == null) {
        event.error(noPlayingTrack)
        null
    } else {
        player
    }
}

fun getPlayerWithQueue(guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayingPlayer(guildId, channelId, event) ?: return null

    return if (player.queue.isEmpty()) {
        event.error(queueEmpty)
        null
    } else {
        player
    }
}
