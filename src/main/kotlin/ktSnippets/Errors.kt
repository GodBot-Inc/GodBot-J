package ktSnippets

import constants.errorRed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun standardError(description: String): MessageEmbed {
    return EmbedBuilder()
        .setDescription(description)
        .setColor(errorRed)
        .build()
}