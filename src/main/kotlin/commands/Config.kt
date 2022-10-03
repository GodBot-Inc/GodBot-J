package commands

import constants.notReceivedParameter
import lib.Mongo
import lib.jda.EventWrapper
import objects.SlashCommandPayload

fun config(event: EventWrapper, payload: SlashCommandPayload) {
    when (event.event.subcommandName) {
        "auto-delete-messages" -> autoDeleteMessages(event, payload)
    }
}

fun autoDeleteMessages(event: EventWrapper, payload: SlashCommandPayload) {
    val minutes = event.getOption("minutes")?.asLong
    if (minutes == null) {
        event.error(notReceivedParameter)
        return
    }

    Mongo.setMessageDeletionTime(payload.guild.id, minutes.toInt())
    if (minutes == 1L)
        event.reply("All messages except queue will be deleted after `$minutes` minute")
    else
        event.reply("All messages except queue will be deleted after `$minutes` minutes")
}
