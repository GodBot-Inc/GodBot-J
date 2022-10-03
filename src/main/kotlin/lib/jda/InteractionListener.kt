package lib.jda

import commands.*
import constants.generalError
import constants.notConnectedToVc
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import objects.SlashCommandPayload
import state.ButtonDistributor
import kotlin.concurrent.thread

class InteractionListener: ListenerAdapter() {

    override fun onSlashCommand(pureEvent: SlashCommandEvent) {
        val dotenv = Dotenv.load()
        val applicationId = dotenv.get("APPLICATIONID")
        val event = EventWrapper(pureEvent)

        val guild = pureEvent.guild
        val member = pureEvent.member
        val voiceChannel = member?.voiceState?.channel

        if (guild == null || member == null) {
            event.error(generalError)
            return
        }
        if (voiceChannel == null) {
            event.error(notConnectedToVc)
            return
        }

        val payload = SlashCommandPayload(
            voiceChannel,
            guild,
            member,
            applicationId
        )

        when (pureEvent.name) {
            "join" -> join(event, payload)
            "play" -> thread { runBlocking { play(event, payload) } }
            "pause" -> pause(event, payload)
            "resume" -> resume(event, payload)
            "stop" -> stop(event, payload)
            "skip" -> skip(event, payload)
            "queue" -> queue(event, payload)
            "clear-queue" -> clearQueue(event, payload)
            "remove" -> remove(event, payload)
            "leave" -> leave(event, payload)
            "loop" -> loop(event, payload)
            "skipto" -> skipTo(event, payload)
            "volume" -> volume(event, payload)
            "forward" -> forward(event, payload)
            "config" -> config(event, payload)
        }
    }

    override fun onButtonClick(event: ButtonClickEvent) {
        ButtonDistributor.distribute(ButtonEventWrapper(event))
    }
}
