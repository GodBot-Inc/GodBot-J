package ktLogging.defaults

import com.andreapivetta.kolor.green
import ktLogging.ChildLogger
import ktLogging.Logger
import ktLogging.LoggingLevel
import ktLogging.database.models.LogImpl
import ktLogging.resolveLoggingLvl

open class LoggerImpl : Logger {
    protected val newGroup = "New Group".green()

    override fun info(msg: String, lvl: LoggingLevel) {
        println(LogImpl(getId(), "info", lvl, msg).toString())
    }

    override fun warning(msg: String, lvl: LoggingLevel) {
        println(LogImpl(getId(), "warning", lvl, msg).toString())
    }

    override fun error(msg: String, lvl: LoggingLevel) {
        println(LogImpl(getId(), "error", lvl, msg).toString())
    }

    override fun fatal(msg: String, lvl: LoggingLevel) {
        println(LogImpl(getId(), "fatal", lvl, msg).toString())
    }

    open fun openGroup(operationTitle: String, lvl: LoggingLevel): ChildLogger {
        val groupId = getId()
        println("$newGroup | ${resolveLoggingLvl(lvl)} | $operationTitle | $groupId")
        return ChildLoggerImpl(groupId)
    }
}
