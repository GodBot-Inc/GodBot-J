package ktCommands.queue.utils

import constants.primary
import constants.queueEmoji
import ktUtils.millisToString
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import objects.AudioTrackExtender
import kotlin.math.ceil

fun compactQueue(queue: ArrayList<AudioTrackExtender>, avatarUrl: String?, page: Int = 1): MessageEmbed {
    val maxPages = ceil(queue.size.toDouble() / 10).toInt()
    val max = page * 10 - 1
    val min = max - 9
    var queueMessage = ""
    println("Max: $max min: $min")

    for (i in min..max) {
        try {
            queueMessage += "[${queue[i].songInfo.title}](${queue[i].songInfo.uri}) " +
                    "- ${millisToString(queue[i].songInfo.duration)}\n\n"
            println("Added song")
        } catch (e: IndexOutOfBoundsException) {
            println("Broke")
            break
        }
    }

    val embed = EmbedBuilder().setDescription(queueMessage).setTitle("${queueEmoji.asMention} Queue")
    embed.setColor(primary)
    embed.setFooter("Page $page/$maxPages", avatarUrl)
    return embed.build()
}
