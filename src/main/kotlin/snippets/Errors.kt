package snippets

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import playableInfo.PlayableInfo
import playableInfo.SpotifySong
import playableInfo.YouTubeSong

fun standardError(description: String): MessageEmbed {
    return EmbedBuilder()
        .setDescription(description)
        .setColor(Colours.godbotWarnOrange)
        .build()
}

fun notFoundError(description: String): MessageEmbed {
    return EmbedBuilder()
        .setDescription(
            String.format(
                "%s **%s**",
                EmojiIds.NotFound,
                description
            )
        )
        .setColor(Colours.godbotWarnOrange)
        .build()
}

fun emptyError(description: String): MessageEmbed {
    return EmbedBuilder()
        .setDescription(
            String.format(
                "%s **%s**",
                EmojiIds.NotFound2,
                description
            )
        )
        .setColor(Colours.godbotWarnOrange)
        .build()
}

private fun testGetAuthor(interpretation: PlayableInfo?): String {
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

private fun testFormatSongSource(playableInfo: PlayableInfo): String {
    println("FormatSongSource")
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
    println("returning")
    return builder.toString()
}

private fun testGetPositionInQueueVideo(position: Int, queueSize: Int): String {
    if (position == 0 && queueSize == 0) {
        return String.format("%s -", EmojiIds.queueEmoji)
    } else if (position == 0) {
        return String.format("%s 1/%s", EmojiIds.queueEmoji, queueSize)
    } else if (queueSize == 0) {
        return String.format("%s %s", EmojiIds.queueEmoji, position)
    }
    return String.format("%s %s/%s", EmojiIds.queueEmoji, position, queueSize)
}

fun testPlayVideo(
    requester: Member,
    nowPlaying: Boolean,
    positionInQueue: Int,
    queueSize: Int
): MessageEmbed {
    println("this method is getting called")
//    val duration: String = if(playableInfo.duration == 0L) String.format(
//        "%s -",
//        trackLinesDefault()
//    ) else String.format(
//        "%s %s",
//        trackLinesDefault(),
//        DurationCalc.longToStringPlus(playableInfo.duration)
//    )

    println("PlayVideo")
    return EmbedBuilder()
//        .setTitle((playableInfo.title) + if (nowPlaying) " Loaded" else " Queued")
//        .setThumbnail(playableInfo.thumbnailUri ?: defaultThumbnail)
//        .addField("Author", testGetAuthor(playableInfo), true)
//        .addField("Sources", testFormatSongSource(playableInfo), true)
//        .addField("Position", testGetPositionInQueueVideo(positionInQueue, queueSize), true)
//        .addField("Duration", duration, false)
        .setColor(Colours.godbotHeavenYellow)
        .setFooter(
            String.format(
                "Added by %s", requester.effectiveName
            ),
            requester.user.avatarUrl
        )
        .build()
}
