package ktLogging.database.models

import com.andreapivetta.kolor.lightGray
import com.godbot.database.models.Log
import ktLogging.*

open class LogImpl(
    override val id: String,
    override val type: String,
    override val lvl: LoggingLevel,
    override val msg: String
): Log {
    override fun toString(): String {
        val standard = "${getDate().lightGray()} | ${resolveLoggingType(type)} | ${resolveLoggingLvl(lvl)} | $msg"
        if (showId) {
            return "$id | $standard"
        }
        return standard
    }
//        return "${getDate()} | $type | ${resolveLoggingLvl(lvl)} | $msg | $id"
}
