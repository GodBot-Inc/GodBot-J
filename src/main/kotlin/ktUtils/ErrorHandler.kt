package ktUtils

import ktLogging.Logger
import ktLogging.LoggingLevel
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktSnippets.standardError
import objects.EventExtender
import objects.SlashCommandPayload

@JvmOverloads
fun handleDefaultErrorResponse(
    event: EventExtender,
    payload: SlashCommandPayload,
    msg: String,
    logger: Logger = GodBotLogger(),
    loggingLevel: LoggingLevel = LoggingLevel.HIGH
){
    event.replyEphemeral(standardError(msg))
    logger.error(formatPayload(payload), loggingLevel)
}