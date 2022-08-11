package ktCommands.queue

import kotlinx.coroutines.runBlocking
import ktCommands.play.services.getYTVideoInfo
import ktCommands.play.utils.convertYtUrlToId
import ktCommands.queue.features.QueueControllableEmbed
import ktCommands.queue.utils.QueueButtons
import ktCommands.queue.utils.compactQueue
import ktCommands.queue.utils.getMaxQueuePages
import ktUtils.getPlayer
import objects.AudioPlayerExtender
import objects.EventFacade
import objects.SlashCommandPayload

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    val player = getPlayer(payload.guild.id, payload.voiceChannel.id, event) ?: return
    runBlocking { populateQueue(player, payload) }
    println("After run Blocking")

    val message = event.replyAction(
        compactQueue(player.queue, payload.member.user.avatarUrl),
        QueueButtons.checkButtons(1, getMaxQueuePages(player.queue))
    )
    QueueControllableEmbed(message, player, event.event.member?.user?.avatarUrl)
}

suspend fun populateQueue(player: AudioPlayerExtender, payload: SlashCommandPayload) {
    val id = convertYtUrlToId("https://music.youtube.com/watch?v=wGNFzQeVrJI&feature=share")
    val info = getYTVideoInfo(id)
    repeat(15) {
        runBlocking { player.play(info, payload) }
    }
}
