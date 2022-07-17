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

fun remove(event: EventExtender, payload: SlashCommandPayload) {
    fun removeCheckParameters(event: SlashCommandEvent): Long {
        val position: OptionMapping = event.getOption("position") ?: throw ArgumentNotFoundException()
        return position.asLong
    }

    val position: Int

    try {
        position = removeCheckParameters(event.event).toInt()
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
    if (player.queue.isEmpty()) {
        event.replyEphemeral(
            standardError(
                ErrorMessages.QUEUE_EMPTY
            )
        )
        return
    }

    val audioTrack: AudioTrackExtender

    try {
        audioTrack = player.removeTrackAt(position - 1)
    } catch (e: IndexOutOfBoundsException) {
        event.replyEphemeral(
            standardError(
                String.format(
                    "There is no Track at Position `%s` there are only" +
                            " `%s` Tracks in the Queue",
                    position,
                    player.queue.size
                )
            )
        )
        return
    }

    event.reply(
        EmbedBuilder()
            .setDescription(
                String.format(
                    "**Removed [%s](%s)**",
                    audioTrack.songInfo.title,
                    audioTrack.songInfo.uri
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}