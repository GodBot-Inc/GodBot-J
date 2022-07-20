package ktCommands.play.utils

import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

fun isValid(url: String): Boolean {
    return try {
        URL(url).toURI()
        true
    } catch (e: MalformedURLException) {
        false
    } catch (e: URISyntaxException) {
        false
    }
}

fun resolvePlatform(url: String): String? {
    return if (url.contains("open.spotify.com"))
        "spotify"
    else if (url.contains("youtube.com") ||
        url.contains("youtu.be"))
        "youtube"
    else null
}

fun isSong(url: String): Boolean? {
    val platform = resolvePlatform(url)
    when (platform) {
        "youtube" -> {
            // TODO: build check for youtu.be urls
            if (url.contains("watch?v=") && !url.contains("list="))
                return true
            return false
        }
    }
    return null
}
