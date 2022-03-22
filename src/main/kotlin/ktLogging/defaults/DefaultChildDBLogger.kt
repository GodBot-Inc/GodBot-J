package ktLogging.defaults

import ktLogging.LoggingLevel
import ktLogging.database.models.LogImpl

class DefaultChildDBLogger(override val groupId: String, private val parentLogger: DefaultDBLogger): ChildLoggerImpl(groupId) {

    override fun info(msg: String, lvl: LoggingLevel) {
        val log = LogImpl(getId(), "info", lvl, msg)
        println("   $log")
        parentLogger.save(log)
    }

    override fun warning(msg: String, lvl: LoggingLevel) {
        val log = LogImpl(getId(), "warning", lvl, msg)
        println("   $log")
        parentLogger.save(log)
    }

    override fun error(msg: String, lvl: LoggingLevel) {
        val log = LogImpl(getId(), "error", lvl, msg)
        println("   $log")
        parentLogger.save(log)
    }

    override fun fatal(msg: String, lvl: LoggingLevel) {
        val log = LogImpl(getId(), "fatal", lvl, msg)
        println("   $log")
        parentLogger.save(log)
    }
}
