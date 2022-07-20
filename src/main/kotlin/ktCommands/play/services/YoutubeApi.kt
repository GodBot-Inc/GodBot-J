package ktCommands.play.services

import kotlinx.coroutines.*
import ktCommands.play.utils.convertYtToMillis
import ktUtils.CouldNotExtractVideoInformation
import ktUtils.UrlBuilder
import ktUtils.VideoNotFoundException
import lib.get
import org.json.JSONException
import org.json.JSONObject
import playableInfo.YouTubePlaylist
import playableInfo.YouTubeSong
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture


val client = HttpClient.newHttpClient()
val builder = HttpRequest.newBuilder()

// TODO: Check for invalid code responses

@Throws(VideoNotFoundException::class, CouldNotExtractVideoInformation::class)
suspend fun getYTVideoInfo(id: String) = coroutineScope {
    var response = get(UrlBuilder.YT().getVideo().id(id).build())
    val builder = YouTubeSong.Builder()

    if (response.getJSONArray("items").isEmpty)
        throw VideoNotFoundException()

    response = response.getJSONArray("items").getJSONObject(0)

    if (response.getJSONObject("snippet") == null
        || response.getJSONObject("contentDetails") == null
        || response.getJSONObject("statistics") == null )
        throw CouldNotExtractVideoInformation()

    val snippet = response.getJSONObject("snippet")
    val contentDetails = response.getJSONObject("contentDetails")
    val statistics = response.getJSONObject("statistics")

    builder.thumbnailUri = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url")
    builder.thumbnailUri = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url")
    if (snippet.getString("channelTitle").contains(" - Topic"))
        builder.creator = snippet.getString("channelTitle").split(" - Topic")[0]
    else
        builder.creator = snippet.getString("channelTitle")
    builder.creatorLink = "https://youtube.com/channel/${snippet.getString("channelId")}"
    builder.title = snippet.getString("title")
    builder.views = statistics.getLong("viewCount")
    if (statistics.getString("likeCount") != "")
        builder.likes = statistics.getLong("likeCount")
    if (statistics.getString("commentCount") != "")
        builder.comments = statistics.getLong("commentCount")
    builder.duration = convertYtToMillis(contentDetails.getString("duration"))
    builder.uri = "https://youtube.com/watch?v=$id"
    builder.songId = id

    return@coroutineScope builder.build()
}

suspend fun getYTPlaylistInfo(id: String) = coroutineScope {
    val infoRequest = builder
        .uri(UrlBuilder.YT().getPlaylist().id(id).build())
        .GET()
        .build()

    val itemsRequest: CompletableFuture<HttpResponse<String>> = client.sendAsync(
        builder.uri(UrlBuilder.YT().getPlaylistItems().id(id).build())
            .GET()
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )
    var response = JSONObject(withContext(Dispatchers.IO) {
        client.send(
            builder.uri(UrlBuilder.YT().getPlaylist().id(id).build())
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    })

    val ytBuilder = YouTubePlaylist.Builder()

    ytBuilder.uri = "https://www.youtube.com/playlist?list=$id"
    if (response.isEmpty)
        throw CouldNotExtractVideoInformation()
    if (response.getJSONArray("items").isEmpty)
        throw CouldNotExtractVideoInformation()

    response = response.getJSONArray("items").getJSONObject(0)

    if (response.getJSONObject("snippet") == null
        || response.getJSONObject("contentDetails") == null)
        throw CouldNotExtractVideoInformation()

    val snippet = response.getJSONObject("snippet")
    val contentDetails = response.getJSONObject("contentDetails")

    if (snippet.getString("channelTitle").contains(" - Topic"))
        ytBuilder.creator = snippet.getString("channelTitle").split(" - Topic")[0]
    else
        ytBuilder.creator = snippet.getString("channelTitle")

    ytBuilder.title = snippet.getString("title")

    ytBuilder.thumbnailUri = try {
        snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url")
    } catch (e: JSONException) {
        snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url")
    }

    val size = contentDetails.getInt("itemCount")
    ytBuilder.size = size

    val durationRequests: ArrayList<Deferred<Long?>> = ArrayList()
    var nextPage = true
    var items = JSONObject(withContext(Dispatchers.IO) {
        itemsRequest.join()
    })
    var processedSongs = 0

    while (nextPage) {
        var nextPageRequest: Deferred<JSONObject>? = null
        try {
            nextPageRequest = async { get(UrlBuilder.YT().getPlaylistItemsToken()
                .id(id)
                .pageToken(items.getString("nextPageToken"))
                .build()) }
        } catch(e: JSONException) {
            nextPage = false
        }

        for (i in 0..items.getJSONArray("items").length()) {
            processedSongs++
            val videoId: String
            try {
                videoId = items.getJSONArray("items")
                    .getJSONObject(i)
                    .getJSONObject("contentDetails")
                    .getString("videoId")
                print(items.getJSONArray("items").getJSONObject(i).getJSONObject("contentDetails").getJSONObject("duration"))
            } catch (e: JSONException) {
                continue
            }
            ytBuilder.addVideoId(videoId)
            durationRequests.add(async { getVideoDuration(videoId) })
        }

        if (processedSongs >= size)
            break

        items = nextPageRequest?.await() ?: break
    }

    for (duration in durationRequests) {
        val finishedDuration = duration.await()
        if (finishedDuration != null)
            ytBuilder.addDuration(finishedDuration)
    }

    return@coroutineScope ytBuilder.build()
}

suspend fun getVideoDuration(id: String): Long? {
    val response = get(UrlBuilder.YT().getVideoDuration().id(id).build())

    return try {
        response
            .getJSONArray("items")
            .getJSONObject(0)
            .getJSONObject("contentDetails")
            .getLong("duration")
    } catch (e: JSONException) {
        null
    }
}
