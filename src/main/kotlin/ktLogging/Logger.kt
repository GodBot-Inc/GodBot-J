package ktLogging
import java.util.*

interface Logger {
    fun getId() = UUID.randomUUID().toString()
    fun info(msg: String, lvl: LoggingLevel = LoggingLevel.HIGH)
    fun warning(msg: String, lvl: LoggingLevel = LoggingLevel.HIGH)
    fun error(msg: String, lvl: LoggingLevel = LoggingLevel.HIGH)
    fun fatal(msg: String, lvl: LoggingLevel = LoggingLevel.HIGH)
}
