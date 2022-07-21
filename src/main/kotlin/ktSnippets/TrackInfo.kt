package ktSnippets

import constants.secondary
import ktUtils.millisToStringDisplay
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import playableInfo.*
import snippets.EmojiIds
import javax.annotation.CheckReturnValue

private const val defaultThumbnail =
    "https://ppsfbg.am.files.1drv.com/y4pE72hePezfSkxt0hQjVD1oB35c-z2BAKrwscGcHyHGLthmiPiwTo9VgtDyscK3-Dg3Kp4I0z0cdu32TP7gZYoRhobdXoEDuZ4sBCyTblSLK-GN4q21X0_x2M6ybSxJkbRcJbd_k0NCp0Qc7lJHqY9TtCE1GSxS8p5R_M2MdtCDzKM5ZDChWflUiXXss2nzuH734US77ThE-ECcb2bHdVGy8YvgL7H7AEmr7tdZnZ5d4w/music.png"

fun trackLines(currentMs: Long, maxMs: Long): String {
    fun roundToNearestFive(nump: Int): Int {
        var num = nump
        if (num % 5 == 0) return num
        else if (num % 5 < 2.5) num -= num % 5
        else num += (5 - num % 5)
        return num
    }

    if (currentMs > maxMs) {
        return trackLinesDefault()
    }

    println("First: " + (currentMs / maxMs.toDouble()))
    println("Second " + (currentMs / maxMs.toDouble() * 100))
    println("Third " + (currentMs / maxMs.toDouble() * 100 / 5))
    println("Fourth " + roundToNearestFive((currentMs / maxMs.toDouble() * 100).toInt()) / 5)

    val position: Int = roundToNearestFive((currentMs / maxMs.toDouble() * 100).toInt()) / 5
    val sb: StringBuilder = StringBuilder()
    for (i in 0..19) {
        println(i)
        if (i == (position - 1)) {
            println(i == position - 1)
            sb.append(":radio_button:")
            continue
        }
        sb.append("▬")
    }
    return sb.toString()
}

fun trackLinesDefault(): String {
    return ":radio_button:▬▬▬▬▬▬▬"
}

private fun getPositionInQueue(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0) {
        return String.format("%s -", EmojiIds.queueEmoji)
    } else if (position == 0) {
        return String.format("%s 1 - %s", EmojiIds.queueEmoji, queueSize)
    } else if (queueSize == 0) {
        return String.format("%s %s", EmojiIds.queueEmoji, position)
    }
    return String.format("%s %s - %s", EmojiIds.queueEmoji, position, queueSize)
}

private fun getPositionInQueueVideo(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0) {
        return String.format("%s -", EmojiIds.queueEmoji)
    } else if (position == 0) {
        return String.format("%s 1/%s", EmojiIds.queueEmoji, queueSize)
    } else if (queueSize == 0) {
        return String.format("%s %s", EmojiIds.queueEmoji, position)
    }
    return String.format("%s %s/%s", EmojiIds.queueEmoji, position, queueSize)
}

private fun getAuthor(interpretation: PlayableInfo?): String {
    interpretation ?: return " - "
    if (interpretation.creatorLink != null) {
        return String.format(
            "[%s](%s)",
            interpretation.creator,
            interpretation.creatorLink
        )
    }
    return String.format(
        "%s",
        interpretation.creator
    )
}

private fun formatSongSource(playableInfo: PlayableInfo): String {
    val builder = StringBuilder()
    if (playableInfo is YouTubeSong) {
        builder.append(
            String.format(
                "%s [YouTube](%s)\n",
                EmojiIds.youtubeEmoji.asMention,
                playableInfo.uri
            )
        )
    } else {
        builder.append(
            String.format(
                "%s -\n",
                EmojiIds.youtubeEmoji.asMention
            )
        )
    }
    if (playableInfo is SpotifySong) {
        builder.append(
            String.format(
                "%s [Spotify](%s)\n",
                EmojiIds.spotifyEmoji,
                playableInfo.uri
            )
        )
    } else {
        builder.append(
            String.format(
                "%s -\n",
                EmojiIds.spotifyEmoji
            )
        )
    }
    return builder.toString()
}

@CheckReturnValue
fun playVideo(
    requester: Member,
    playableInfo: PlayableInfo,
    positionInQueue: Int,
    queueSize: Int
): MessageEmbed {
    val duration: String = if(playableInfo.duration == 0L) String.format(
        "%s -",
        trackLinesDefault()
    ) else String.format(
        "%s %s",
        trackLinesDefault(),
        millisToStringDisplay(playableInfo.duration)
    )

    return EmbedBuilder()
        .setTitle(playableInfo.title)
        .setThumbnail(playableInfo.thumbnailUri ?: defaultThumbnail)
        .addField("Creator", getAuthor(playableInfo), true)
        .addField("Sources", formatSongSource(playableInfo), true)
        .addField("Position", getPositionInQueueVideo(positionInQueue, queueSize), true)
        .addField("Duration", duration, false)
        .setColor(secondary)
        .setFooter(
            String.format(
                "Added by %s", requester.effectiveName
            ),
            requester.user.avatarUrl
        )
        .build()
}

private fun formatPlaylistSource(playlistInfo: PlaylistPlayableInfo): String {
    val stringBuilder = StringBuilder()
    if (playlistInfo is YouTubePlaylist && playlistInfo.uri != null) {
        stringBuilder.append(
            String.format(
                "%s [YouTube](%s)\n",
                EmojiIds.youtubeEmoji.asMention,
                playlistInfo.uri,
            )
        )
    } else {
        stringBuilder.append(
            String.format(
                String.format(
                    " %s YouTube -\n",
                    EmojiIds.youtubeEmoji.asMention
                )
            )
        )
    }
    if (playlistInfo is SpotifyPlaylist && playlistInfo.uri != null) {
        stringBuilder.append(
            String.format(
                "%s [Spotify](%s)\n",
                EmojiIds.spotifyEmoji,
                playlistInfo.uri
            )
        )
    } else {
        stringBuilder.append(
            String.format(
                "%s -\n",
                EmojiIds.spotifyEmoji
            )
        )
    }
    return stringBuilder.toString()
}

@CheckReturnValue
fun playPlaylist(
    requester: Member,
    playlistInfo: PlaylistPlayableInfo,
    positionInQueue: Int,
    queueSize: Int
): MessageEmbed {
    val duration: String = if(playlistInfo.duration == 0L) String.format(
        "%s -",
        trackLinesDefault()
    ) else String.format(
        "%s %s",
        trackLinesDefault(),
        millisToStringDisplay(playlistInfo.duration)
    )

    return EmbedBuilder()
        .setTitle(playlistInfo.title)
        .setThumbnail(playlistInfo.thumbnailUri ?: defaultThumbnail)
        .addField(
            "Tracks",
            String.format(
                "%s %s",
                EmojiIds.trackEmoji,
                playlistInfo.size
            ),
            true
        )
        .addField("Sources", formatPlaylistSource(playlistInfo), true)
        .addField("Position", getPositionInQueue(positionInQueue, queueSize), true)
        .addField("Total Duration", duration, false)
        .setColor(secondary)
        .setFooter(
            String.format(
                "Added by %s", requester.effectiveName
            ),
            requester.user.avatarUrl
        )
        .build()
}
