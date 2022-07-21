package ktUtils

import constants.errorRed
import ktLogging.Logger
import ktLogging.LoggingLevel
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import net.dv8tion.jda.api.EmbedBuilder
import objects.EventFacade
import objects.SlashCommandPayload

@JvmOverloads
fun handleDefaultErrorResponse(
    event: EventFacade,
    payload: SlashCommandPayload,
    msg: String,
    logger: Logger = GodBotLogger(),
    loggingLevel: LoggingLevel = LoggingLevel.HIGH
){
    event.replyEphemeral(
        EmbedBuilder()
            .setDescription(msg)
            .setColor(errorRed)
            .build()
    )
    logger.error(formatPayload(payload), loggingLevel)
}