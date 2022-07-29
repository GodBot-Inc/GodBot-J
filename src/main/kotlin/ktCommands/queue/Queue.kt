package ktCommands.queue

import objects.EventFacade
import objects.SlashCommandPayload

fun queue(event: EventFacade, payload: SlashCommandPayload) {
    event.reply("Sorry, but the Queue command is currently being reworked, so it'll not work.")
}
