package utils.apis.youtube;

import com.mongodb.util.JSON;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.customExceptions.LinkInterpretation.youtubeApi.ApiKeyNotRetreived;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.RequestFailed;
import utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfo;
import utils.linkProcessing.interpretations.youtube.YoutubePlaylistInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class youtubeApi {

    private static final String getPlaylistInfoUrl =
            "https://youtube.googleapis.com/youtube/v3/playlists?" +
                    "part=snippet&part=contentDetails&id=%s&maxResults=1&key=%s";
    private static final String getPlaylistItemsUrl =
            "https://youtube.googleapis.com/youtube/v3/playlistItems?" +
                    "part=snippet&playlistId=%s&key=%s";
    private static final String getVideoInformationUrl =
            "https://youtube.googleapis.com/youtube/v3/videos?" +
                    "part=contentDetails&part=snippet&part=statistics&id=%s&maxResults=1&key=%s";

    private static long convertDuration(String duration) {
        // TODO Convert duration from this format: {insert format} into a long (milliseconds)
        return 0;
    }

    private static String getApiKey() throws ApiKeyNotRetreived {
        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("YT_API_KEY");
        if (API_KEY == null) {
            throw new ApiKeyNotRetreived("Youtube Api key is null " + API_KEY);
        }
        return API_KEY;
    }

    private static YoutubeVideoInterpretation extractVideoInfo(
            JSONObject snippet,
            JSONObject statistics,
            JSONObject contentDetails,
            String id
    ) throws CouldNotExtractInfo {

        if (snippet.isEmpty()) {
            throw new CouldNotExtractInfo("Snippet is empty " + snippet);
        }
        if (statistics.isEmpty()) {
            throw new CouldNotExtractInfo("Statistics is empty " + statistics);
        }

        String thumbnail;
        if (snippet.getJSONObject("thumbnails").isEmpty()) {
            thumbnail = null;
        } else {
            thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
        }

        String author = snippet.getString("channelTitle");
        if (author.contains(" - Topic")) {
            author = author.split(" - Tpoic")[0];
        }

        String title = snippet.getString("title");

        long viewCount = Integer.parseInt(statistics.getString("viewCount"));

        long likes;
        if (Objects.equals(statistics.getString("likeCount"), "")) {
            likes = 0;
        } else {
            likes = Integer.parseInt(statistics.getString("likeCount"));
        }

        long dislikes;
        if (Objects.equals(statistics.getString("likeCount"), "")) {
            dislikes = 0;
        } else {
            dislikes = Integer.parseInt(statistics.getString("dislikeCount"));
        }

        long comments;
        if (Objects.equals(statistics.getString("commentCount"), "")) {
            comments = 0;
        } else {
            comments = Integer.parseInt(statistics.getString("commentCount"));
        }

        long duration;
        if (contentDetails.isEmpty()) {
            duration = 0;
        } else {
            duration = convertDuration(contentDetails.getString("duration"));
        }

        return new YoutubeVideoInterpretation(
                duration,
                author,
                title,
                String.format(
                        "https://www.youtube.com/watch?v=%s",
                        id
                ),
                thumbnail,
                likes,
                dislikes,
                viewCount,
                comments
        );
    }

    public static YoutubeVideoInterpretation getVideoInformation(String id)
            throws IOException, RequestFailed, InvalidURL {
        JSONObject videoInfo = sendRequest(String.format(
                getVideoInformationUrl,
                id,
                getApiKey()
        ));

        // Check and extract received information
        if (videoInfo.isEmpty()) {
            throw new CouldNotExtractInfo("JSONObject is empty");
        }
        if (videoInfo.getJSONObject("items").isEmpty()) {
            throw new CouldNotExtractInfo("Items is empty");
        }
        return extractVideoInfo(
                videoInfo.getJSONObject("items").getJSONObject("snippet"),
                videoInfo.getJSONObject("items").getJSONObject("statistics"),
                videoInfo.getJSONObject("items").getJSONObject("contentDetails"),
                id
        );
    }

    public static YoutubePlaylistInterpretation getPlaylistInformation(String id)
            throws InvalidURL, IOException, RequestFailed, CouldNotExtractInfo {
        // TODO Combine PlaylistInfo and Playlistitems requests in one method
        JSONObject playlistInfo = sendRequest(
                String.format(
                        getPlaylistInfoUrl,
                        id,
                        getApiKey()
                )
        );
        JSONObject playlistItems = sendRequest(
                String.format(
                        getPlaylistItemsUrl,
                        id,
                        getApiKey()
                )
        );

        if (playlistInfo.isEmpty()) {
            throw new CouldNotExtractInfo("PlaylistInfo is empty");
        }
        if (playlistInfo.getJSONObject("items").isEmpty()) {
            throw new CouldNotExtractInfo("Items is empty");
        }

        if (playlistItems.isEmpty()) {
            throw new CouldNotExtractInfo("PlaylistItems is empty");
        }
        if (playlistItems.getJSONObject("items").isEmpty()) {
            throw new CouldNotExtractInfo("Items is empty");
        }

        JSONObject snippet = playlistItems.getJSONObject("items").getJSONObject("snippet");

        String author = snippet.getString("channelTitle");
        if (author.contains(" - Topic")) {
            author = author.split(" - Topic")[0];
        }

        String title = snippet.getString("title");

        String thumbnail;
        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
        } else {
            thumbnail = null;
        }

        int itemCount = Integer.parseInt(playlistInfo.getJSONObject("contentDetails").getString("itemCount"));

        long duration = 0;

        ArrayList<YoutubeVideoInterpretation> videoInterpretations = new ArrayList<>();
        JSONArray array = playlistItems.getJSONArray("items");
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            duration += convertDuration(
                    obj.
                            getJSONObject("contentDetails").
                            getString("duration")
            );

            videoInterpretations.add(
                    extractVideoInfo(
                            obj.getJSONObject("snippet"),
                            obj.getJSONObject("statistics"),
                            obj.getJSONObject("contentDetails"),
                            id
                    )
            );
        }

        return new YoutubePlaylistInterpretation(
                duration,
                author,
                title,
                String.format(
                        "https://www.youtube.com/playlist?list=%s",
                        id
                ),
                thumbnail,
                itemCount,
                videoInterpretations
        );
    }

    private static void checkResponseCode(int code) throws RequestFailed, InvalidURL {
        switch (code) {
            case 200, 201, 202, 203, 204 -> System.out.println("Request was successful");
            case 400, 401, 403 -> throw new RequestFailed("The request that was sent failed");
            case 404 -> throw new InvalidURL("The request returned a 404 error");
            case 500, 501, 502, 503, 504 -> throw new InternalError("Youtube has some issues resolving this request");
        }
    }

    private static JSONObject sendRequest(String url) throws IOException, RequestFailed, InvalidURL {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        checkResponseCode(responseCode);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return new JSONObject(response.toString());
    }
}
