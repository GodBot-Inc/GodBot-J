package lib.jda

import constants.errorRed
import constants.primary
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction
import java.awt.Color

class EventFacade(event: SlashCommandEvent) {

    val event: SlashCommandEvent

    init {
        this.event = event
    }

    fun replyEphemeral(embed: MessageEmbed) {
        this.event.replyEmbeds(embed).setEphemeral(true).queue()
    }

    fun replyLink(message: String, color: Color = primary) {
        this.reply(
            EmbedBuilder()
                .setDescription("**$message**")
                .setColor(color)
                .build()
        )
    }

    fun replyEmote(emote: Emoji, message: String, color: Color = primary) {
        this.reply(
            EmbedBuilder()
                .setTitle("${emote.asMention} $message")
                .setColor(color)
                .build()
        )
    }

    fun replyEmoteLink(emote: Emoji, message: String, color: Color = primary) {
        this.reply(
            EmbedBuilder()
                .setDescription("${emote.asMention} **$message**")
                .setColor(color)
                .build()
        )
    }

    fun reply(embed: MessageEmbed)  {
        this.event.replyEmbeds(embed).queue()
    }

    fun replyAction(embed: MessageEmbed, buttons: ArrayList<Button>?): ReplyAction {
        if (buttons == null)
            return this.event.replyEmbeds(embed)
        return this.event.replyEmbeds(embed).addActionRow(buttons)
    }

    fun error(message: String) {
        this.replyEphemeral(
            EmbedBuilder()
                .setDescription(message)
                .setColor(errorRed)
                .build()
        )
    }

    fun getHook(): InteractionHook = this.event.hook

    fun deferReply() {
        this.event.deferReply().queue()
    }

    fun getOption(optionName: String): OptionMapping? {
        return this.event.getOption(optionName)
    }

    fun getLong(optionName: String): Long? {
        return this.event.getOption(optionName)?.asLong
    }
}
