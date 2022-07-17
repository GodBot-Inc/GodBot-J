package ktCommands

import ktSnippets.standardError
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.EmojiIds
import snippets.ErrorMessages
import utils.EventExtender

fun volume(event: EventExtender, payload: SlashCommandPayload) {
    fun volumeCheckParameters(event: SlashCommandEvent): Long {
        val level: OptionMapping = event.getOption("level") ?: throw ArgumentNotFoundException()
        return level.asLong
    }

    val level: Int

    try {
        level = volumeCheckParameters(event.event).toInt()
    } catch (e: CheckFailedException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(payload.applicationId),
            payload.guild.id
        )
    if (player == null) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }

    if (player.voiceChannel.id != payload.voiceChannel.id) {
        event.replyEphemeral(standardError(ErrorMessages.NO_PLAYER_IN_VC))
        return
    }
    if(player.queue.isEmpty()) {
        event.replyEphemeral(standardError(ErrorMessages.QUEUE_EMPTY))
        return
    }

    val playerVolume: Int = player.getVolume()
    var emojiId: String = EmojiIds.noAudioChange

    if (playerVolume > level*10) {
        emojiId = EmojiIds.quieter
    } else if (playerVolume < level*10) {
        emojiId = EmojiIds.louder
    }
    if (level == 0) {
        emojiId = EmojiIds.mute
    }

    player.setVolume(level*10)

    event.reply(
        EmbedBuilder()
            .setDescription(
                String.format(
                    "%s **Set Volume to level to %s**",
                    emojiId,
                    level
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}