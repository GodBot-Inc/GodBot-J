package utils.apis.youtube;

import com.mongodb.util.JSON;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.customExceptions.LinkInterpretation.youtubeApi.ApiKeyNotRetreived;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.RequestFailed;
import utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfo;
import utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFound;
import utils.linkProcessing.interpretations.youtube.YoutubePlaylistInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

// TODO Make youtubeApi section VideoType 10 (music) only so we can use youtube music links
public class youtubeApi {

    private static final String getPlaylistInfoUrl =
            "https://youtube.googleapis.com/youtube/v3/" +
                    "playlists?" +
                    "part=snippet" +
                    "&part=contentDetails" +
                    "&id=%s" +
                    "&maxResults=1" +
                    "&key=%s";
    private static final String getPlaylistItemsUrl =
            "https://youtube.googleapis.com/youtube/v3/" +
                    "playlistItems?" +
                    "part=snippet" +
                    "&part=contentDetails" +
                    "&playlistId=%s" +
                    "&key=%s";
    private static final String getVideoInformationUrl =
            "https://youtube.googleapis.com/youtube/v3/" +
                    "videos?" +
                    "part=contentDetails" +
                    "&part=snippet" +
                    "&part=statistics" +
                    "&id=%s" +
                    "&maxResults=1" +
                    "&key=%s";
    private static final String playlistItemsUrlWithToken =
            "https://youtube.googleapis.com/youtube/v3/" +
                    "playlistItems?" +
                    "part=snippet" +
                    "&part=contentDetails" +
                    "&pageToken=%s" +
                    "&playlistId=%s" +
                    "&key=%s";

    /**
     * Converts the duration from the passed String that we get from YouTube to a long
     * @param duration passed from YouTube
     * @return duration in milliseconds
     */
    private static long convertDuration(String duration) {
        long time = 0;
        if (duration.contains("PT") || duration.contains("PM")) {
            duration = duration.substring(2);
        }
        if (duration.contains("H")) {
            // multiply hours with 3600000 to get milliseconds
            time += Long.parseLong(duration.split("H")[0]) * 3600000;
        }
        if (duration.contains("M")) {
            if (time != 0) {
                time += Long.parseLong(duration.split("M")[0].substring(1)) * 60000;
            } else {
                time += Long.parseLong(duration.split("M")[0]) * 60000;
            }
        }
        if (duration.contains("S")) {
            if (duration.contains("M")) {
                time += Long.parseLong(duration.split("M")[1].substring(0, 1)) * 1000;
            } else if (duration.contains("H")) {
                time += Long.parseLong(duration.split("H")[1].substring(0, 1)) * 1000;
            } else {
                time += Long.parseLong(duration.substring(0, 1)) * 1000;
            }
        }
        return time;
    }

    private static String getApiKey() throws ApiKeyNotRetreived {
        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("YT_API_KEY");
        if (API_KEY == null) {
            throw new ApiKeyNotRetreived("Youtube Api key is null " + API_KEY);
        }
        return API_KEY;
    }

    /**
     * Call it and pass the following arguments to get a YoutubeVideoInterpretation
     * @param snippet The snippet part of the original JSONObject
     * @param statistics The statistics part of the original JSONObject
     * @param contentDetails The contentDetails part of the original JSONObject
     * @param id The YouTube id from the Video you want to extract the information from
     * @return A YoutubeVideoInterpretation that contains all the gathered information
     * @throws CouldNotExtractInfo If one of the necessary parts is empty
     */
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
        if (author.contains("- Topic")) {
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

        long duration = convertDuration(contentDetails.getString("duration"));

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

    /**
     * This function gathers information about the video with the given id
     * @param id The YouTube id of the video that should be gathered information about
     * @return Returns a YoutubeVideoInterpretation Object which contains all the gathered information
     * @throws IOException If the request sending failed
     * @throws RequestFailed If the request returns an invalid response code
     * @throws InvalidURL If the Url the method got from the id is invalid
     * @throws CouldNotExtractInfo If the Video Information could not be extracted because YouTubes answer is
     * missing something important
     * @throws VideoNotFound If the given id is not an id of a YouTube Video
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static YoutubeVideoInterpretation getVideoInformation(String id)
            throws IOException, RequestFailed, InvalidURL, CouldNotExtractInfo, VideoNotFound, InternalError {
        JSONObject videoInfo = sendRequest(String.format(
                getVideoInformationUrl,
                id,
                getApiKey()
        ));

        if (videoInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFound("VideoId: " + id + " not found");
        }

        JSONObject items = videoInfo.getJSONArray("items").getJSONObject(0);
        if (items.isEmpty()) {
            throw new VideoNotFound(String.format("VideoId: %s could not be found", id));
        }
        return extractVideoInfo(
                items.getJSONObject("snippet"),
                items.getJSONObject("statistics"),
                items.getJSONObject("contentDetails"),
                id
        );
    }

    /**
     * The only purpose of the method is to return the converted duration from a video
     * @param id The YouTube id of the song
     * @return Only the duration of the song in milliseconds
     * @throws InvalidURL If the URL that he got from the id is not valid
     * @throws IOException When the send command of the request failed
     * @throws RequestFailed If the request returned an invalid return code
     * @throws InternalError If YouTube has issues resolving the request
     */
    private static long getVideoDuration(String id)
            throws InvalidURL, IOException, RequestFailed, InternalError {
        return convertDuration(sendRequest(
                String.format(
                        getVideoInformationUrl,
                        id,
                        getApiKey()
                )
        )
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("contentDetails")
                        .getString("duration")
        );
    }

    private static boolean checkToken(JSONObject jsonObject) {
        try {
            jsonObject.get("nextPageToken");
        } catch(JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * The purpose of the method is to gather information about a playlist
     * @param id The YouTube id of the playlist
     * @return a YoutubePlaylistInterpretation Object that inhabits all important information
     * @throws InvalidURL gets thrown if the Url it got from the id is invalid
     * @throws IOException When the send command of the request failed
     * @throws RequestFailed If the request  returned an invalid return code
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static YoutubePlaylistInterpretation getPlaylistInformation(String id)
            throws InvalidURL, IOException, RequestFailed, InternalError {
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
        if (playlistInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFound("PlaylistId: " + id + " could not be found");
        }

        JSONObject contentDetails;
        JSONObject snippet;
        try {
            contentDetails = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfo("ContentDetails was not found");
        }
        try {
            snippet = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfo("Snippet was not found");
        }

        String author = snippet.getString("channelTitle");
        if (author.contains("Topic")) {
            author = author.split(" - Topic")[0];
        }

        String title = snippet.getString("title");

        String thumbnail;
        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
        } else {
            thumbnail = null;
        }

        int itemCount = contentDetails.getInt("itemCount");

        long duration = 0;

        ArrayList<String> videoIds = new ArrayList<>();
        JSONObject playlistItemsObject = playlistItems;
        JSONArray array;
        while (checkToken(playlistItemsObject)) {
            array = playlistItemsObject.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                String videoId = array
                        .getJSONObject(i)
                        .getJSONObject("contentDetails")
                        .getString("videoId");

                duration += getVideoDuration(videoId);
                videoIds.add(videoId);
            }
            playlistItemsObject = sendRequest(
                    String.format(
                            playlistItemsUrlWithToken,
                            playlistItemsObject.getString("nextPageToken"),
                            id,
                            getApiKey()
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
                videoIds
        );
    }

    /**
     * A method that is there for sending requests
     * @param url The destination that a request will be sent to
     * @return the response of the website as JSONObject
     * @throws IOException If the request failed
     * @throws RequestFailed If the request returned an invalid return code
     * @throws InvalidURL If the passed URL is invalid
     * @throws InternalError If the website has trouble processing the request
     */
    private static JSONObject sendRequest(String url)
            throws IOException, RequestFailed, InvalidURL, InternalError {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        switch (responseCode) {
            case 400, 401, 403 -> throw new RequestFailed("The request that was sent failed");
            case 404 -> throw new InvalidURL("The request returned a 404 error");
            case 500, 501, 502, 503, 504 -> throw new InternalError("The site has some issues resolving your request");
        }

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
