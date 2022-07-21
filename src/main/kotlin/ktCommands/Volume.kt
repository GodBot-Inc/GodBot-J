package ktCommands

import constants.*
import ktUtils.getPlayerWithQueue
import net.dv8tion.jda.api.entities.Emoji
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun volume(event: EventFacade, payload: SlashCommandPayload) {
    val level: Int? = event.getOption("level")?.asLong?.toInt()
    if (level == null) {
        event.error(notReceivedParameter)
        return
    }

    val player = getPlayerWithQueue(
        JDAManager.getInstance().getJDA(payload.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    val playerVolume: Int = player.getVolume()
    var emoji: Emoji = noAudioChangeEmoji

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