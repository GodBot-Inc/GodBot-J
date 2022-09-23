package functions

import constants.primary
import constants.queueEmoji
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import state.AudioTrackExtender
import utils.millisToString
import kotlin.math.ceil


fun getMaxQueuePages(queue:ArrayList<AudioTrackExtender>): Int {
    if (queue.isEmpty())
        return 0
    return ceil(queue.size.toDouble() / 10).toInt()
}

fun compactQueue(queue: ArrayList<AudioTrackExtender>, avatarUrl: String?, page: Int = 1): MessageEmbed {
    if (queue.isEmpty()) {
        return EmbedBuilder()
            .setDescription("Waiting for Tracks...")
            .setTitle("${queueEmoji.asMention} Queue")
            .setColor(primary)
            .build()
    }
    val maxPages = getMaxQueuePages(queue)
    val max = page * 10 - 1
    val min = max - 9
    var queueMessage = ""

    for (i in min..max) {
        try {
            queueMessage += "${i+1}. [${queue[i].songInfo.title}](${queue[i].songInfo.uri}) " +
                    "- ${millisToString(queue[i].songInfo.duration)}\n\n"
        } catch (e: IndexOutOfBoundsException) {
            break
        }
    }

    val embed = EmbedBuilder().setDescription(queueMessage).setTitle("${queueEmoji.asMention} Queue")
    embed.setColor(primary)
    embed.setFooter("Page $page/$maxPages", avatarUrl)
    return embed.build()
}
