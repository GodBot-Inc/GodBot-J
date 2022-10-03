package commands

import constants.*
import lib.jda.EventWrapper
import net.dv8tion.jda.api.entities.Emoji
import objects.SlashCommandPayload
import functions.getPlayingPlayer

fun volume(event: EventWrapper, payload: SlashCommandPayload) {
    val level: Int? = event.getLong("level")?.toInt()

    val player = getPlayingPlayer(
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val playerVolume: Int = player.getVolume()
    var emoji: Emoji = noAudioChangeEmoji

    if (level == null) {
        emoji = if (playerVolume/10 >= 7)
            maxVolumeEmoji
        else if (playerVolume/10 >= 4)
            mediumVolumeEmoji
        else
            lowVolumeEmoji
        event.replyEmote(emoji, "Volume: ${playerVolume/10}")
        return
    }

    if (playerVolume > level*10) {
        emoji = quieterEmoji
    } else if (playerVolume < level*10) {
        emoji = louderEmoji
    }
    if (level == 0) {
        emoji = muteEmoji
    }

    player.setVolume(level*10)
    event.replyEmote(emoji, "Set Volume to level $level")
}