package ktUtils

fun millisToString(millis: Long): String {
    var duration = millis
    if (duration == 0L)
        return "00:00"

    var hours = 0
    var minutes = 0
    var seconds = 0

    if (duration >= 3600000) {
        hours = (duration % 3600000).toInt()
        duration -= 3600000 * hours
    }
    if (duration >= 60000) {
        minutes = (duration % 60000).toInt()
        duration -= 60000 * minutes
    }
    if (duration >= 1000) {
        seconds = (duration % 1000).toInt()
        duration -= 1000 * seconds
    }

    var strHours = ""
    var strMinutes = ""
    var strSeconds = ""
    if (hours >= 10)
        strHours = "$hours"
    else if (hours in 1..9)
        strHours = "0$hours"
    if (minutes >= 10)
        strMinutes = "$minutes"
    else if (minutes in 1..9)
        strMinutes = "0$minutes"
    if (seconds >= 10)
        strSeconds = "$seconds"
    else if (seconds in 1..9)
        strSeconds = "0$seconds"

    if (strHours != "")
        return "$strMinutes:$strSeconds"
    return "$strHours:$strMinutes:$strSeconds"
}

fun millisToStringDisplay(millis: Long): String {
    val duration = millisToString(millis)
    return when (duration.split(":").size) {
        3 -> "**00:00:00 - $duration**"
        2 -> "**00:00 - $duration**"
        else -> "**00 - $duration**"
    }
}
