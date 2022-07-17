package ktUtils

import ktSnippets.standardError
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import snippets.Colours
import java.awt.Color

class EventExtender(event: SlashCommandEvent) {

    val event: SlashCommandEvent

    init {
        this.event = event
    }

    fun replyEphemeral(message: String, color: Color = Colours.godbotYellow) {
        this.replyEphemeral(
            EmbedBuilder()
                .setTitle(message)
                .setColor(color)
                .build()
        )
    }

    fun replyEphemeral(embed: MessageEmbed) {
        this.event.replyEmbeds(embed).queue()
    }

    fun reply(message: String, color: Color = Colours.godbotYellow) {
        this.reply(
            EmbedBuilder()
                .setTitle(message)
                .setColor(color)
                .build()
        )
    }

    fun replyLink(message: String, color: Color = Colours.godbotYellow) {
        this.reply(
            EmbedBuilder()
                .setDescription("**$message**")
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
        this.event.replyEmbeds(embed).queue()
    }

    fun error(message: String) {
        this.replyEphemeral(standardError(message))
    }

    fun clearError(message: String) {
        this.reply(standardError(message))
    }

    fun getTextChannel(): TextChannel = this.event.textChannel

    fun getHook(): InteractionHook = this.event.hook

    fun deferReply() {
        this.event.deferReply().queue()
    }

    fun getOption(optionName: String): OptionMapping? {
        return this.event.getOption(optionName)
    }
}
