package commands.play.utils

import constants.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import objects.playableInformation.*
import utils.millisToStringDisplay
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

    val position: Int = roundToNearestFive((currentMs / maxMs.toDouble() * 100).toInt()) / 5
    val sb: StringBuilder = StringBuilder()
    for (i in 0..19) {
        if (i < (position - 1))
            sb.append("[▬](https://godbot-music.com)")
        else if (i == (position - 1))
            sb.append(":radio_button:")
        else
            sb.append("▬")
    }
    return sb.toString()
}

fun trackLinesDefault(): String {
    return ":radio_button:▬▬▬▬▬▬▬"
}

private fun getPositionInQueue(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0) {
        return String.format("%s -", queueEmoji.asMention)
    } else if (position == 0) {
        return String.format("%s 1 - %s", queueEmoji.asMention, queueSize)
    } else if (queueSize == 0) {
        return String.format("%s %s", queueEmoji.asMention, position)
    }
    return String.format("%s %s - %s", queueEmoji.asMention, position, queueSize)
}

private fun getPositionInQueueVideo(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0) {
        return String.format("%s -", queueEmoji.asMention)
    } else if (position == 0) {
        return String.format("%s 1/%s", queueEmoji.asMention, queueSize)
    } else if (queueSize == 0) {
        return String.format("%s %s", queueEmoji.asMention, position)
    }
    return String.format("%s %s/%s", queueEmoji.asMention, position, queueSize)
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
                youtubeEmoji.asMention,
                playableInfo.uri
            )
        )
    } else {
        builder.append(
            String.format(
                "%s -\n",
                youtubeEmoji.asMention
            )
        )
    }
    if (playableInfo is SpotifySong) {
        builder.append(
            String.format(
                "%s [Spotify](%s)\n",
                spotifyEmoji.asMention,
                playableInfo.uri
            )
        )
    } else {
        builder.append(
            String.format(
                "%s -\n",
                spotifyEmoji.asMention
            )
        )
    }
    return builder.toString()
}

@CheckReturnValue
fun playVideoMessage(
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
                youtubeEmoji.asMention,
                playlistInfo.uri,
            )
        )
    } else {
        stringBuilder.append(
            String.format(
                String.format(
                    " %s YouTube -\n",
                    youtubeEmoji.asMention
                )
            )
        )
    }
    if (playlistInfo is SpotifyPlaylist && playlistInfo.uri != null) {
        stringBuilder.append(
            String.format(
                "%s [Spotify](%s)\n",
                spotifyEmoji.asMention,
                playlistInfo.uri
            )
        )
    } else {
        stringBuilder.append(
            String.format(
                "%s -\n",
                spotifyEmoji.asMention
            )
        )
    }
    return stringBuilder.toString()
}

@CheckReturnValue
fun playPlaylistMessage(
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
                trackSizeEmoji.asMention,
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
