package ktCommands

import commands.Command
import ktUtils.getPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.EmojiIds
import snippets.ErrorMessages

fun loop(event: EventFacade, payload: SlashCommandPayload) {
    val mode = event.getOption("mode")?.asBoolean
    if (mode == null) {
        event.error(ErrorMessages.NOT_RECEIVED_PARAMETER)
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
        event.replyEmote(EmojiIds.loop, "Loop Mode Enabled")
    else
        event.replyEmote(EmojiIds.noLoop, "Loop Mode Disabled")
}