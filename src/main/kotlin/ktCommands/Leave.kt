package ktCommands

import commands.Command
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktSnippets.standardError
import ktUtils.*
import net.dv8tion.jda.api.EmbedBuilder
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.Colours
import snippets.ErrorMessages
import utils.EventExtender

fun leave(event: EventExtender, payload: SlashCommandPayload) {
    val logger = GodBotLogger().command(
        "leave",
        formatPayload(payload)
    )

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
