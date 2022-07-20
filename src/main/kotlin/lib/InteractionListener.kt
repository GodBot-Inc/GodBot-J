package lib

import commands.Queue
import commands.Resume
import commands.Skip
import commands.Stop
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.runBlocking
import ktCommands.*
import ktCommands.play.play
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import snippets.ErrorMessages

class InteractionListener: ListenerAdapter() {

    override fun onSlashCommand(pureEvent: SlashCommandEvent) {
        val dotenv = Dotenv.load()
        val applicationId = dotenv.get("APPLICATIONID")
        val event = EventExtender(pureEvent)

        val guild = pureEvent.guild
        val member = pureEvent.member
        val voiceChannel = member?.voiceState?.channel

        if (guild == null || member == null) {
            event.error(ErrorMessages.GENERAL_ERROR)
            return
        }
        if (voiceChannel == null) {
            event.error(ErrorMessages.NOT_CONNECTED_TO_VC)
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
            "play" -> runBlocking { play(event, payload) }
            "pause" -> pause(event, payload)
            "resume" -> Resume.trigger(event, payload)
            "stop" -> Stop.trigger(event, payload)
            "skip" -> Skip.trigger(event, payload)
            "queue" -> Queue.trigger(event, payload)
            "clear-queue" -> clearQueue(event, payload)
            "remove" -> remove(event, payload)
            "leave" -> leave(event, payload)
            "loop" -> loop(event, payload)
            "skipto" -> skipTo(event, payload)
            "volume" -> volume(event, payload)
            "seek" -> seek(event, payload)
        }
    }
}