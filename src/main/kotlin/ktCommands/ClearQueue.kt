package ktCommands

import commands.Command
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import net.dv8tion.jda.api.EmbedBuilder
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.EmojiIds
import snippets.ErrorMessages
import lib.EventExtender
import lib.SlashCommandPayload

fun clearQueue(event: EventExtender, payload: SlashCommandPayload) {
    val logger = GodBotLogger().command(
        "clearQueue",
        formatPayload(payload)
    )

    val player = PlayerVault
        .getInstance()
        .getPlayer(
            JDAManager.getInstance().getJDA(Command.applicationId),
            payload.guild.id
        )
    if (player == null || player.voiceChannel.id != payload.voiceChannel.id) {
        event.error(ErrorMessages.NO_PLAYER_IN_VC)
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
