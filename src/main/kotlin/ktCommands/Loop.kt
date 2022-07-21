package ktCommands

import commands.Command
import constants.loopEmoji
import constants.noLoopEmoji
import constants.notReceivedParameter
import ktUtils.getPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager

fun loop(event: EventFacade, payload: SlashCommandPayload) {
    val mode = event.getOption("mode")?.asBoolean
    if (mode == null) {
        event.error(notReceivedParameter)
        return
    }

    val player = getPlayer(
        JDAManager.getInstance().getJDA(Command.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    if (player.loop == mode && player.loop) {
        event.error("This Player is already in Loop Mode")
        return
    } else if (player.loop == mode) {
        event.error("The Loop mode is already disabled")
        return
    }

    player.loop = mode
    if (mode)
        event.replyEmote(loopEmoji, "Loop Mode Enabled")
    else
        event.replyEmote(noLoopEmoji, "Loop Mode Disabled")
}