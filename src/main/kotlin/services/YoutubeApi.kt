package services

import commands.play.utils.convertYtToMillis
import commands.play.utils.convertYtUrlToId
import kotlinx.coroutines.*
import lib.get
import objects.playableInformation.YouTubePlaylist
import objects.playableInformation.YouTubeSong
import org.json.JSONException
import org.json.JSONObject
import utils.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture


val client: HttpClient = HttpClient.newHttpClient()
val builder: HttpRequest.Builder = HttpRequest.newBuilder()

// TODO: Check for invalid code responses

suspend fun getYTVideoInfoFromUrl(url: String) = getYTVideoInfo(convertYtUrlToId(url))

@Throws(VideoNotFoundException::class, CouldNotExtractVideoInformation::class)
suspend fun getYTVideoInfo(id: String) = coroutineScope {
    var response = get(YTUrlBuilder().getVideo().id(id).build())
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

    try {
        builder.thumbnailUri = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url")
    } catch (e:JSONException) {
        builder.thumbnailUri = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url")
    }

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

@Throws(PlaylistNotFoundException::class, CouldNotExtractVideoInformation::class, JSONException::class)
suspend fun getYTPlaylistInfo(url: String) = coroutineScope {
    println("URL inside Playlist: $url")
    val id = convertYtUrlToId(url)
    val itemsRequest: CompletableFuture<HttpResponse<String>> = client.sendAsync(
        builder.uri(YTUrlBuilder().getPlaylistItems().id(id).build())
            .GET()
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )
    var response = get(YTUrlBuilder().getPlaylist().id(id).build())

    val ytBuilder = YouTubePlaylist.Builder()

    ytBuilder.uri = "https://www.youtube.com/playlist?list=$id"
    if (response.isEmpty)
        throw PlaylistNotFoundException()
    if (response.getJSONArray("items").isEmpty)
        throw PlaylistNotFoundException()

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

    val infoRequests: ArrayList<Deferred<YouTubeSong>> = ArrayList()
    val itemsResponse = withContext(Dispatchers.IO) {
        itemsRequest.join()
    }

    println("StatusCode ${itemsResponse.statusCode()}")
    var items = JSONObject(itemsResponse.body())
    var processedSongs = 0
    var nextPageRequest: Deferred<JSONObject>? = null
    var nextPageToken: String?

    while (true) {
        nextPageToken = try {
            items.getString("nextPageToken")
        } catch(e: JSONException) {
            null
        }

        if (nextPageToken != null)
            nextPageRequest = async { get(
                YTUrlBuilder().getPlaylistItemsToken()
                    .id(id)
                    .pageToken(nextPageToken)
                    .build()) }

        for (i in 0..items.getJSONArray("items").length()) {
            processedSongs++
            val videoId: String
            try {
                videoId = items.getJSONArray("items")
                    .getJSONObject(i)
                    .getJSONObject("contentDetails")
                    .getString("videoId")
            } catch (e: JSONException) {
                continue
            }
            ytBuilder.addVideoId(videoId)
            infoRequests.add(async { getYTVideoInfo(videoId) })
        }

        if (processedSongs >= size)
            break

        try {
            items = nextPageRequest?.await() ?: break
        } catch (e: JSONException) {
            break
        }
    }

    for (infoDeferred in infoRequests) {
        val info = try {
            infoDeferred.await()
        } catch (e: YouTubeApiException) {
            continue
        }

        ytBuilder.addSongInfo(info)
        ytBuilder.addDuration(info.duration)
    }

    return@coroutineScope ytBuilder.build()
}
