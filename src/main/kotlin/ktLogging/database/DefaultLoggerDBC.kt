package ktLogging.database

import com.godbot.database.models.GroupLog
import com.godbot.database.models.Log
import io.github.cdimascio.dotenv.Dotenv
import ktLogging.LoggingLevel
import ktLogging.defaults.LoggerImpl

object DefaultLoggerDBC: LoggerDBC {

    val logger = LoggerImpl()

    init {
        logger.info("Initializing DefaultLoggerDBC", LoggingLevel.LOW)
        val dotenv = Dotenv.load()
        val username = dotenv.get("DBUSERNAME")
        val pw = dotenv.get("DBPASSWORD")

//        val connectionString = ConnectionString(
//            String.format(
//                ,
//                username,
//                pw
//            )
//        )


    }

    override fun saveLog(log: Log) {
        TODO("Not yet implemented")
    }

    override fun saveGroupLog(log: GroupLog) {
        TODO("Not yet implemented")
    }
}
