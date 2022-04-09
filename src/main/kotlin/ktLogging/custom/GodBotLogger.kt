package ktLogging.custom

import ktLogging.LoggingLevel
import ktLogging.database.models.LogImpl
import ktLogging.defaults.LoggerImpl

class GodBotLogger: LoggerImpl() {

    fun info(msg: String) {
        super.info(msg, LoggingLevel.HIGH)
    }

    fun warning(msg: String) {
        super.warning(msg, LoggingLevel.HIGH)
    }

    fun error(msg: String) {
        super.error(msg, LoggingLevel.HIGH)
    }

    fun fatal(msg: String) {
        super.fatal(msg, LoggingLevel.HIGH)
    }

    fun command(commandName: String, msg: String): GodBotChildLogger {
        val groupId = getId()
        println(
            LogImpl(
                groupId,
                "info",
                LoggingLevel.HIGH,
                "$commandName triggered $msg"
            ).toString()
        )
        return GodBotChildLogger(groupId)
    }
}
