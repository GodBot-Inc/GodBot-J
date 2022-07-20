package ktCommands

import commands.Command
import lib.EventExtender
import lib.SlashCommandPayload
import singeltons.JDAManager
import singeltons.PlayerVault
import snippets.ErrorMessages

fun leave(event: EventExtender, payload: SlashCommandPayload) {
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

    player.closeConnection()
    event.reply("Left Channel ${payload.voiceChannel.name}")
}
