package ktCommands.play.utils

import io.github.cdimascio.dotenv.Dotenv
import java.net.URI

class YTUrlBuilder {
    private var url: String = ""
    private var pageToken: String = ""
    private var id: String = ""

    fun pageToken(pageToken: String) = apply { this.pageToken = pageToken }
    fun id(id: String) = apply { this.id = id }

    fun getVideo() = apply {
        this.url = "https://youtube.googleapis.com/youtube/v3/" +
                "videos?" +
                "part=contentDetails" +
                "&part=snippet" +
                "&part=statistics" +
                "&id=idHere" +
                "&maxResults=1" +
                "&key=apiKeyHere"
    }

    fun getPlaylist() = apply {
        this.url = "https://youtube.googleapis.com/youtube/v3/" +
                "playlists?" +
                "part=snippet" +
                "&part=contentDetails" +
                "&id=idHere" +
                "&maxResults=1" +
                "&key=apiKeyHere"
    }

    fun getPlaylistItems() = apply {
        this.url = "https://youtube.googleapis.com/youtube/v3/playlistItems?" +
                "part=snippet" +
                "&part=contentDetails" +
                "&playlistId=idHere" +
                "&key=apiKeyHere"
    }

    fun getPlaylistItemsToken() = apply {
        this.url = "https://youtube.googleapis.com/youtube/v3/" +
                "playlistItems?" +
                "part=snippet" +
                "&part=contentDetails" +
                "&pageToken=pageTokenHere" +
                "&playlistId=idHere" +
                "&key=apiKeyHere"
    }

    private fun getApiKey(): String = Dotenv.load()["YT_API_KEY"]

    fun buildString(): String {
        var result = url

        if (url.contains("pageTokenHere"))
            result = result.replace("pageTokenHere", this.pageToken)
        if (url.contains("idHere"))
            result = result.replace("idHere", this.id)
        if (url.contains("idHere"))
            result = result.replace("idHere", this.id)
        if (url.contains("apiKeyHere"))
            result = result.replace("apiKeyHere", getApiKey())

        return result
    }

    fun build(): URI = URI.create(buildString())
}