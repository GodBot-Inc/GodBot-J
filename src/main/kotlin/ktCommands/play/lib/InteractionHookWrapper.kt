package ktCommands.play.lib

import constants.errorRed
import constants.primary
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import java.awt.Color

class InteractionHookWrapper(hook: InteractionHook) {
    private val hook: InteractionHook

    init {
        this.hook = hook
    }

    fun error(message: String) {
        this.hook.sendMessageEmbeds(EmbedBuilder().setDescription(message).setColor(errorRed).build()).queue()
    }

    fun reply(message: String, color: Color = primary) {
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