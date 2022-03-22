package com.godbot.database.models

import ktLogging.LoggingLevel

interface GroupLog {
    val id: String
    val type: String
    val lvl: LoggingLevel
    val title: String
    val childLogs: ArrayList<Log>
}
