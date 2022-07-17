package ktUtils

import ktSnippets.standardError
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import snippets.Colours

import java.awt.*

class EventExtender(val event: SlashCommandEvent) {

    fun replyEphemeral(message: String, color: Color = Colours.godbotYellow) {
        this.replyEphemeral(
            EmbedBuilder()
                .setTitle(message)
                .setColor(color)
                .build()
        )
    }

    fun replyEphemeral(embed: MessageEmbed) {
        this.event.replyEmbeds(embed)
    }

    fun reply(message: String, color: Color = Colours.godbotYellow) {
        this.reply(
            EmbedBuilder()
                .setTitle(message)
                .setColor(color)
                .build()
        )
    }

    fun replyEmote(emote: Emoji, message: String, color: Color = Colours.godbotYellow) {
        this.reply(
            EmbedBuilder()
                .setDescription("${emote.asMention}**${message}**")
                .setColor(color)
                .build()
        )
    }

    fun reply(embed: MessageEmbed) {
        this.event.replyEmbeds(embed)
    }

    fun error(message: String) {
        this.replyEphemeral(standardError(message))
    }

    fun clearError(message: String) {
        this.reply(standardError(message))
    }

    fun getOption(optionName: String): OptionMapping? {
        return this.event.getOption(optionName)
    }
}
