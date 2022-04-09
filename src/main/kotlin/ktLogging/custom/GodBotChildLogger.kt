package ktLogging.custom

import ktLogging.LoggingLevel
import ktLogging.defaults.ChildLoggerImpl

class GodBotChildLogger(override val groupId: String): ChildLoggerImpl(groupId) {
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
}