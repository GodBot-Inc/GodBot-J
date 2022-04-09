package ktCommands

import commands.Command.applicationId
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktSnippets.standardError
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import singeltons.AudioPlayerManagerWrapper
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.EmojiIds
import snippets.ErrorMessages
import utils.EventExtender

fun clearQueue(event: EventExtender, payload: SlashCommandPayload) {
    val logger = GodBotLogger().command(
        "clearQueue",
        formatPayload(payload)
    )

    val player: AudioPlayerExtender

    try {
        player = PlayerVault
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
                payload.guild.id
            )
    } catch (e: GuildNotFoundException) {
        handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_GUILD, logger)
        return
    } catch (e: ChannelNotFoundException) {
        handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger)
        return
    }
    logger.info("Got AudioPlayer")

    if (player.voiceChannel.id != payload.voiceChannel.id) {
        handleDefaultErrorResponse(event, payload, ErrorMessages.NO_PLAYER_IN_VC, logger)
        return
    }

    player.clearQueue()

    event.reply(
        EmbedBuilder()
            .setTitle(
                "${EmojiIds.cleaned} Removed all tracks from the Queue"
            )
            .setColor(Colours.godbotYellow)
            .build()
    )
    logger.info("Response sent")
}

fun leave(event: EventExtender, payload: SlashCommandPayload) {
    val logger = GodBotLogger().command(
        "leave",
        formatPayload(payload)
    )

    val player: AudioPlayerExtender

    try {
        player = AudioPlayerManagerWrapper
            .getInstance()
            .getPlayer(
                JDAManager.getInstance().getJDA(applicationId),
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
