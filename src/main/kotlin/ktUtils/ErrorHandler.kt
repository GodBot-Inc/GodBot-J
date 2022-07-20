package ktUtils

import ktLogging.Logger
import ktLogging.LoggingLevel
import ktLogging.custom.GodBotLogger
import ktLogging.formatPayload
import ktSnippets.standardError
import lib.EventExtender
import lib.SlashCommandPayload
import net.dv8tion.jda.api.interactions.InteractionHook

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

@JvmOverloads
fun handleInteractionHookErrorResponse(
    interactionHook: InteractionHook,
    payload: SlashCommandPayload,
    msg: String,
    logger: Logger = GodBotLogger(),
    loggingLevel: LoggingLevel = LoggingLevel.HIGH
) {
    interactionHook.sendMessageEmbeds(standardError(msg)).queue()
    logger.error(formatPayload(payload), loggingLevel)
}
