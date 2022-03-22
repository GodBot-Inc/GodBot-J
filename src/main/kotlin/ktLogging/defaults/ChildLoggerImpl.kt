package ktLogging.defaults

import ktLogging.ChildLogger
import ktLogging.LoggingLevel
import ktLogging.database.models.LogImpl

open class ChildLoggerImpl(override val groupId: String) : ChildLogger {
    override fun info(msg: String, lvl: LoggingLevel) {
        println("   ${LogImpl(getId(), "info", lvl, msg)}")
    }

    override fun warning(msg: String, lvl: LoggingLevel) {
        println("   ${LogImpl(getId(), "warning", lvl, msg)}")
    }

    override fun error(msg: String, lvl: LoggingLevel) {
        println("   ${LogImpl(getId(), "error", lvl, msg)}")
    }

    override fun fatal(msg: String, lvl: LoggingLevel) {
        println("   ${LogImpl(getId(), "fatal", lvl, msg)}}")
    }
}
