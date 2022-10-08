package commands

import constants.notReceivedParameter
import lib.Mongo
import lib.jda.EventWrapper
import net.dv8tion.jda.api.entities.Guild

fun config(event: EventWrapper, guild: Guild) {
    when (event.event.subcommandName) {
        "auto-delete-messages" -> autoDeleteMessages(event, guild)
    }
}

fun autoDeleteMessages(event: EventWrapper, guild: Guild) {
    val minutes = event.getOption("minutes")?.asLong
    if (minutes == null) {
        event.error(notReceivedParameter)
        return
    }

    Mongo.setMessageDeletionTime(guild.id, minutes.toInt())
    if (minutes == 1L)
        event.reply("All messages except queue will be deleted after `$minutes` minute")
    else
        event.reply("All messages except queue will be deleted after `$minutes` minutes")
}
