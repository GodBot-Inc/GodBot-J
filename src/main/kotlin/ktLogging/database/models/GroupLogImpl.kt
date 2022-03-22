package com.godbot.database.models

import ktLogging.LoggingLevel

open class GroupLogImpl(
    override val id: String,
    override val type: String,
    override val lvl: LoggingLevel,
    override val title: String,
    override val childLogs: ArrayList<Log>
): GroupLog