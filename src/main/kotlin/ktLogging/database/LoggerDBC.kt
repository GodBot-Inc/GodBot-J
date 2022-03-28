package ktLogging.database

import com.godbot.database.models.GroupLog
import com.godbot.database.models.Log

interface LoggerDBC {

    fun saveLog(log: Log)

    fun saveGroupLog(log: GroupLog)
}
