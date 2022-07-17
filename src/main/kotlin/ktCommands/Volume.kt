package ktCommands

import ktUtils.*
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.EmojiIds
import snippets.ErrorMessages
import ktUtils.EventExtender
import net.dv8tion.jda.api.entities.Emoji

fun volume(event: EventExtender, payload: SlashCommandPayload) {
    val level: Int? = event.getOption("level")?.asLong?.toInt()
    if (level == null) {
        event.error(ErrorMessages.NOT_RECEIVED_PARAMETER)
        return
    }

    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(payload.applicationId),
            payload.guild.id
        )
    if (player == null || player.voiceChannel.id != payload.voiceChannel.id) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }

    if(player.queue.isEmpty()) {
        event.error(ErrorMessages.QUEUE_EMPTY)
        return
    }

    val playerVolume: Int = player.getVolume()
    var emoji: Emoji = EmojiIds.noAudioChange

    if (playerVolume > level*10) {
        emoji = EmojiIds.quieter
    } else if (playerVolume < level*10) {
        emoji = EmojiIds.louder
    }
    if (level == 0) {
        emoji = EmojiIds.mute
    }

    player.setVolume(level*10)
    event.replyEmote(emoji, "Set Volume to level $level")
}