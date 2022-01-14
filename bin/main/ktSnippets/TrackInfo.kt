package ktSnippets

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import playableInfo.*
import snippets.Colours
import snippets.EmojiIds
import utils.DurationCalc
import javax.annotation.CheckReturnValue

private const val defaultThumbnail =
    "https://ppsfbg.am.files.1drv.com/y4pE72hePezfSkxt0hQjVD1oB35c-z2BAKrwscGcHyHGLthmiPiwTo9VgtDyscK3-Dg3Kp4I0z0cdu32TP7gZYoRhobdXoEDuZ4sBCyTblSLK-GN4q21X0_x2M6ybSxJkbRcJbd_k0NCp0Qc7lJHqY9TtCE1GSxS8p5R_M2MdtCDzKM5ZDChWflUiXXss2nzuH734US77ThE-ECcb2bHdVGy8YvgL7H7AEmr7tdZnZ5d4w/music.png"

fun trackLines(currentMs: Long, maxMs: Long): String {
    val position: Long = currentMs / (maxMs / 20)
    val sb: StringBuilder = StringBuilder()
    for (i in 1..21) {
        if (i == (position - 1).toInt()) {
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
                EmojiIds.youtubeEmoji,
                playableInfo.uri
            )
        )
    } else {
        builder.append(
            String.format(
                "%s -\n",
                EmojiIds.youtubeEmoji
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
        DurationCalc.longToStringPlus(playableInfo.duration)
    )

    return EmbedBuilder()
        .setTitle(playableInfo.title)
        .setThumbnail(playableInfo.thumbnailUri ?: defaultThumbnail)
        .addField("Creator", getAuthor(playableInfo), true)
        .addField("Sources", formatSongSource(playableInfo), true)
        .addField("Position", getPositionInQueueVideo(positionInQueue, queueSize), true)
        .addField("Duration", duration, false)
        .setColor(Colours.godbotHeavenYellow)
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
                EmojiIds.youtubeEmoji,
                playlistInfo.uri,
            )
        )
    } else {
        stringBuilder.append(
            String.format(
                String.format(
                    " %s YouTube -\n",
                    EmojiIds.youtubeEmoji
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
        DurationCalc.longToStringPlus(playlistInfo.duration)
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
        .setColor(Colours.godbotHeavenYellow)
        .setFooter(
            String.format(
                "Added by %s", requester.effectiveName
            ),
            requester.user.avatarUrl
        )
        .build()
}
