package ktCommands

import commands.Command
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

fun loop(event: EventExtender, payload: SlashCommandPayload) {
    fun loopCheckParameter(event: SlashCommandEvent): Boolean {
        val mode: OptionMapping = event.getOption("mode") ?: throw ArgumentNotFoundException()
        return mode.asBoolean
    }

    val mode: Boolean
    try {
        mode = loopCheckParameter(event.event)
    } catch (e: ArgumentNotFoundException) {
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
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id
        )
    if (player == null) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
        return
    }

    if (player.voiceChannel.id != payload.voiceChannel.id) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NO_PLAYER_IN_VC
            )
        )
        return
    }

    if (player.loop == mode && player.loop) {
        event.replyEphemeral(standardError("The Player is already in Loop Mode"))
        return
    } else if (player.loop == mode) {
        event.replyEphemeral(standardError("Loop mode is already disabled"))
        return
    }

    player.loop = mode

    event.reply(
        EmbedBuilder()
            .setTitle(
                if (mode) {
                    "${EmojiIds.loop} Loop Mode Enabled"
                } else {
                    "${EmojiIds.noLoop} Loop Mode Disabled"
                }
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}