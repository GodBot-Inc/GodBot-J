package ktCommands

import commands.Command
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
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
                JDAManager.getInstance().getJDA(Command.applicationId),
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
