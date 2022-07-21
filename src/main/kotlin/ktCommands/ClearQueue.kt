package ktCommands

import commands.Command
import ktUtils.getPlayerWithQueue
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.EmojiIds

fun clearQueue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayerWithQueue(
        JDAManager.getInstance().getJDA(Command.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    player.clearQueue()

    event.replyEmote(EmojiIds.cleaned, "Removed all tracks from the Queue")
}
