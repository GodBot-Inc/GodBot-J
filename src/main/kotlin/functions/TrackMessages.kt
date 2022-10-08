package functions

import constants.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import objects.playableInformation.*
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

    if (currentMs > maxMs)
        return trackLinesDefault()

    val position: Int = roundToNearestFive((currentMs / maxMs.toDouble() * 100).toInt()) / 5
    val builder: StringBuilder = StringBuilder()
    for (i in 0..19) {
        if (i < (position - 1))
            builder.append("[▬](https://godbot-music.com)")
        else if (i == (position - 1))
            builder.append(":radio_button:")
        else
            builder.append("▬")
    }
    return builder.toString()
}

fun trackLinesDefault() = ":radio_button:▬▬▬▬▬▬▬"

private fun getDuration(duration: Long) = if(duration == 0L) "${trackLinesDefault()} -"
                                          else "${trackLinesDefault()} ${millisToStringDisplay(duration)}"

private fun getPositionInQueue(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0)
        return "${queueEmoji.asMention} -"
    else if (position == 0)
        return "${queueEmoji.asMention} 1 - $queueSize"
    else if (queueSize == 0)
        return "${queueEmoji.asMention} - $position"
    return "${queueEmoji.asMention} $position - $queueSize"
}

private fun getPositionInQueueVideo(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0)
        return "${queueEmoji.asMention} -"
    else if (position == 0)
        return "${queueEmoji.asMention} 1/${queueSize}"
    else if (queueSize == 0)
        return "${queueEmoji.asMention} $position"
    return "${queueEmoji.asMention} $position/$queueSize"
}

private fun getAuthor(info: PlayableInfo?): String {
    info ?: return " - "
    if (info.creatorLink != null)
        return "[${info.creator}](${info.creatorLink})"
    return info.creator
}

private fun formatSongSource(playableInfo: PlayableInfo): String {
    val builder = StringBuilder()

    if (playableInfo is YouTubeSong)
        builder.append("${youtubeEmoji.asMention} [YouTube](${playableInfo.uri})\n")
    else
        builder.append("${youtubeEmoji.asMention} -\n")

    if (playableInfo is SpotifySong)
        builder.append("${spotifyEmoji.asMention} [Spotify](${playableInfo.uri})\n")
    else
        builder.append("${spotifyEmoji.asMention} -\n")

    return builder.toString()
}

@CheckReturnValue
fun playVideoMessage(
    requester: Member,
    playableInfo: PlayableInfo,
    positionInQueue: Int,
    queueSize: Int
): MessageEmbed {
    val duration: String = getDuration(playableInfo.duration)

    return EmbedBuilder()
        .setTitle(playableInfo.title)
        .setThumbnail(playableInfo.thumbnailUri ?: defaultThumbnail)
        .addField("Creator", getAuthor(playableInfo), true)
        .addField("Sources", formatSongSource(playableInfo), true)
        .addField("Position", getPositionInQueueVideo(positionInQueue, queueSize), true)
        .addField("Duration", duration, false)
        .setColor(secondary)
        .setFooter("Added by ${requester.effectiveName}", requester.user.avatarUrl)
        .build()
}

private fun formatPlaylistSource(playlistInfo: PlaylistPlayableInfo): String {
    val builder = StringBuilder()

    if (playlistInfo is YouTubePlaylist && playlistInfo.uri != null)
        builder.append("${youtubeEmoji.asMention} [YouTube](${playlistInfo.uri})\n")
    else
        builder.append("${youtubeEmoji.asMention} -\n")
    if (playlistInfo is SpotifyPlaylist && playlistInfo.uri != null)
        builder.append("${spotifyEmoji.asMention} [Spotify](${playlistInfo.uri})\n")
    else
        builder.append("${spotifyEmoji.asMention} -\n")

    return builder.toString()
}

@CheckReturnValue
fun playPlaylistMessage(
    requester: Member,
    playlistInfo: PlaylistPlayableInfo,
    positionInQueue: Int,
    queueSize: Int
): MessageEmbed {
    val duration: String = getDuration(playlistInfo.duration)

    return EmbedBuilder()
        .setTitle(playlistInfo.title)
        .setThumbnail(playlistInfo.thumbnailUri ?: defaultThumbnail)
        .addField("Tracks", "${trackSizeEmoji.asMention} ${playlistInfo.size}", true)
        .addField("Sources", formatPlaylistSource(playlistInfo), true)
        .addField("Position", getPositionInQueue(positionInQueue, queueSize), true)
        .addField("Total Duration", duration, false)
        .setColor(secondary)
        .setFooter("Added by ${requester.effectiveName}", requester.user.avatarUrl)
        .build()
}
