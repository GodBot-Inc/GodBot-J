package lib.jda

import constants.errorRed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import java.util.concurrent.TimeUnit

class InteractionHookWrapper(hook: InteractionHook) {
    private val hook: InteractionHook

    init {
        this.hook = hook
    }

    fun error(message: String) {
        this.hook.sendMessageEmbeds(EmbedBuilder().setDescription(message).setColor(errorRed).build()).submit().thenCompose {
            it.delete().submitAfter(30, TimeUnit.SECONDS)
        }
    }

    fun reply(embed: MessageEmbed) {
        this.hook.sendMessageEmbeds(embed).submit().thenCompose {
            it.delete().submitAfter(30, TimeUnit.SECONDS)
        }
    }
}