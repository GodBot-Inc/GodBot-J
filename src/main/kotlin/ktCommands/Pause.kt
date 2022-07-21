package ktCommands

import commands.Command
import ktUtils.getPlayingPlayer
import objects.EventFacade
import objects.SlashCommandPayload
import singeltons.JDAManager
import snippets.EmojiIds

fun pause(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayingPlayer(
        JDAManager.getInstance().getJDA(Command.applicationId),
        payload.guild.id,
        payload.voiceChannel.id,
        event
    ) ?: return

    if (player.isPaused()) {
        event.error("Player is already paused")
        return
    }

    player.setPaused(true)
    event.replyEmote(EmojiIds.pause, "Player paused")
}
