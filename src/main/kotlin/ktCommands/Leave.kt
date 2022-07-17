package ktCommands

import commands.Command
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktSnippets.standardError
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import snippets.Colours
import snippets.ErrorMessages
import utils.EventExtender

fun leave(event: EventExtender, payload: SlashCommandPayload) {
    val logger = GodBotLogger().command(
        "leave",
        formatPayload(payload)
    )

    val player: AudioPlayerExtender

    try {
        player = AudioPlayerManagerWrapper
            .getInstance()
            .getOrCreatePlayer(
                JDAManager.getInstance().getJDA(Command.applicationId),
                payload.guild.id,
                payload.voiceChannel
            )
    } catch (e: JDANotFoundException) {
        handleDefaultErrorResponse(event, payload, ErrorMessages.PLAYER_NOT_FOUND, logger)
        return
    } catch (e: GuildNotFoundException) {
        handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_GUILD, logger)
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

    player.stop()
    player.closeConnection()

    event.reply(
        EmbedBuilder()
            .setTitle(
                String.format(
                    "Left Channel %s",
                    payload.voiceChannel.name
                )
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
}
