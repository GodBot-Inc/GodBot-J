package ktCommands.play.lib

import ktSnippets.standardError
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import snippets.Colours
import java.awt.Color

class InteractionHookWrapper(hook: InteractionHook) {
    val hook: InteractionHook

    init {
        this.hook = hook
    }

    fun error(message: String) {
        this.hook.sendMessageEmbeds(standardError(message)).queue()
    }

    fun reply(message: String, color: Color = Colours.godbotYellow) {
        this.reply(
            EmbedBuilder()
                .setTitle(message)
                .setColor(color)
                .build()
        )
    }

    fun reply(embed: MessageEmbed) {
        this.hook.sendMessageEmbeds(embed).queue()
    }
}