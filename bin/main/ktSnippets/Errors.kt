package ktSnippets

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import snippets.Colours
import snippets.EmojiIds

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
