package lib.jda

import constants.errorRed
import lib.Mongo
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
        val deletionTime = Mongo.getMessageDeletionTime(hook.interaction.guild?.id)
        val request = hook.sendMessageEmbeds(EmbedBuilder().setDescription(message).setColor(errorRed).build())
        if (deletionTime != null)
            request.submit().thenCompose {
                it.delete().submitAfter(TimeUnit.MINUTES.toSeconds(deletionTime.toLong()), TimeUnit.SECONDS)
            }
        else
            request.queue()
    }

    fun reply(embed: MessageEmbed) {
        val deletionTime = Mongo.getMessageDeletionTime(hook.interaction.guild?.id)
        val request = hook.sendMessageEmbeds(embed)
        if (deletionTime != null)
            request.submit().thenCompose {
                it.delete().submitAfter(TimeUnit.MINUTES.toSeconds(deletionTime.toLong()), TimeUnit.SECONDS)
            }
        else
            request.queue()
    }
}
