package ktLogging
import java.util.*

interface Logger {
    fun getId() = UUID.randomUUID().toString()
    fun info(msg: String, lvl: LoggingLevel)
    fun warning(msg: String, lvl: LoggingLevel)
    fun error(msg: String, lvl: LoggingLevel)
    fun fatal(msg: String, lvl: LoggingLevel)
}
