package ktUtils

import net.dv8tion.jda.api.JDA
import objects.AudioPlayerExtender
import objects.EventFacade
import singeltons.PlayerVault
import snippets.ErrorMessages


fun getPlayer(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = PlayerVault.getInstance().getPlayer(jda, guildId)
    return if (player == null || player.voiceChannel.id != channelId) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        null
    } else {
        player
    }
}

fun getPlayingPlayer(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayer(jda, guildId, channelId, event) ?: return null

    return if (player.currentTrack == null) {
        event.error(ErrorMessages.NO_PLAYING_TRACK)
        null
    } else {
        player
    }
}

fun getPlayerWithQueue(jda: JDA, guildId: String, channelId: String, event: EventFacade): AudioPlayerExtender? {
    val player = getPlayingPlayer(jda, guildId, channelId, event) ?: return null

    return if (player.queue.isEmpty()) {
        event.error(ErrorMessages.QUEUE_EMPTY)
        null
    } else {
        player
    }
}
