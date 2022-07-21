package ktUtils

import constants.noPlayerInVc
import constants.noPlayingTrack
import constants.queueEmpty
import net.dv8tion.jda.api.JDA
import objects.AudioPlayerExtender
import objects.EventFacade
import singeltons.PlayerVault


fun getPlayer(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = PlayerVault.getInstance().getPlayer(jda, guildId)
    return if (player == null || player.voiceChannel.id != channelId) {
        event.error(noPlayerInVc)
        null
    } else {
        player
    }
}

fun getPlayingPlayer(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayer(jda, guildId, channelId, event) ?: return null

    return if (player.currentTrack == null) {
        event.error(noPlayingTrack)
        null
    } else {
        player
    }
}

fun getPlayerWithQueue(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayingPlayer(jda, guildId, channelId, event) ?: return null

    return if (player.queue.isEmpty()) {
        event.error(queueEmpty)
        null
    } else {
        player
    }
}
