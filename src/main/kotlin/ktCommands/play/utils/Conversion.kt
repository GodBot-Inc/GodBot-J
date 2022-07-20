package ktCommands.play.utils

fun convertYtToMillis(durationp: String): Long {
    var duration = durationp
    var time: Long = 0

    if (duration.contains("PT") || duration.contains("PM"))
        duration = duration.substring(2)

    if (duration.contains("H"))
        // hours ot milliseconds
        time += duration.split("H")[0].toLong() * 3600000

    if (duration.contains("M")) {
        time += if (duration.contains("H"))
            // minutes to milliseconds
            duration.split("H")[1].split("M")[0].toLong() * 60000
        else
            duration.split("M")[0].toLong() * 60000
    }

    if (duration.contains("S")) {
        time += if (duration.contains("M"))
            duration.split("M")[1].split("S")[0].toLong() * 1000
        else if (duration.contains("H"))
            duration.split("H")[1].split("S")[0].toLong() + 1000
        else
            duration.split("S")[0].toLong() * 1000
    }

    return time
}

fun convertYtUrlToId(url: String): String {
    return if (url.contains("youtu.be/"))
        url.split(".be/")[1]
    else if (url.contains("list="))
        url.split("list=")[1].split("&")[0]
    else
        url.split("watch\\?v=")[1].split("&")[0]
}
