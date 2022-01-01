package snippets

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun standardError(description: String): MessageEmbed {
    return EmbedBuilder()
        .setTitle(
            String.format(
                "%s",
                description
            )
        )
        .setColor(Colours.godbotWarnOrange)
        .build()
}

fun standardErrorWithLink(description: String): MessageEmbed {
    return EmbedBuilder()
        .setDescription(
            String.format(
                "**%s**",
                description
            )
        )
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
        .setTitle(
            String.format(
                "%s **%s**",
                EmojiIds.NotFound2,
                description
            )
        )
        .setColor(Colours.godbotWarnOrange)
        .build()
}
