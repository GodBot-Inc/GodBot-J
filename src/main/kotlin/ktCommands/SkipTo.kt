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
import snippets.ErrorMessages
import utils.EventExtender

fun skipTo(event: EventExtender, payload: SlashCommandPayload) {
    fun skipCheckParameter(event: SlashCommandEvent): Long {
        val position: OptionMapping = event.getOption("position") ?: throw ArgumentNotFoundException()
        return position.asLong
    }

    val position: Long
    try {
        position = skipCheckParameter(event.event)
    } catch (e: ArgumentNotFoundException) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.NOT_RECEIVED_PARAMETER
            )
        )
        return
    }

    val player: AudioPlayerExtender
    try {
        player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(Command.applicationId),
                payload.guild.id
            )
    } catch (e: PlayerNotFoundException) {
        event.replyEphemeral(standardError(ErrorMessages.NO_PLAYER_IN_VC))
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
    if (player.queue.isEmpty()) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.QUEUE_EMPTY
            )
        )
        return
    }

    try {
        player.skipTo((position - 1).toInt())
    } catch (e: IndexOutOfBoundsException) {
        event.replyEphemeral(
            standardError(
                "The Queue is `${player.queue.size}` big and the position is `$position`"
            )
        )
        return
    }

    if (player.currentTrack == null) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.LOADING_FAILED
            )
        )
    }

    event.reply(
        EmbedBuilder()
            .setDescription("Skipped to `$position`, now playing " +
                    "[${player.currentTrack!!.songInfo.title}](${player.currentTrack!!.songInfo.uri})")
            .setColor(Colours.godbotYellow)
            .build()
    )
}