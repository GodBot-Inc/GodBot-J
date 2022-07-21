package lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

val client: HttpClient = HttpClient.newHttpClient()
val builder: HttpRequest.Builder = HttpRequest.newBuilder()

suspend fun get(uri: URI): JSONObject = coroutineScope {
    val response = withContext(Dispatchers.IO) {
        client.send(
            builder.GET().uri(uri).build(),
            BodyHandlers.ofString()
        )
    }
    return@coroutineScope JSONObject(response)
}
