package com.godbot.utils.apis.youtube;

import com.godbot.utils.audio.DurationCalc;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.RequestException;
import com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfoException;
import com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFoundException;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import com.godbot.utils.linkProcessing.LinkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class YoutubeApi {

    /**
     * Call it and pass the following arguments to get a YoutubeVideoInterpretation
     * @param snippet The snippet part of the original JSONObject
     * @param statistics The statistics part of the original JSONObject
     * @param contentDetails The contentDetails part of the original JSONObject
     * @param id The YouTube id from the Video you want to extract the information from
     * @return A YoutubeVideoInterpretation that contains all the gathered information
     * @throws CouldNotExtractInfoException If one of the necessary parts is empty
     */
    private static YoutubeVideoInterpretation extractVideoInfo(
            JSONObject snippet,
            JSONObject statistics,
            JSONObject contentDetails,
            String id
    ) throws CouldNotExtractInfoException {
        if (snippet.isEmpty()) {
            throw new CouldNotExtractInfoException("Snippet is empty " + snippet);
        }
        if (statistics.isEmpty()) {
            throw new CouldNotExtractInfoException("Statistics is empty " + statistics);
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

        String authorLink = snippet.getString("channelId");

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

        long duration = DurationCalc.ytStringToLong(contentDetails.getString("duration"));

        return new YoutubeVideoInterpretation(
                duration,
                author,
                authorLink,
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
     * @throws RequestException If the request returns an invalid response code
     * @throws InvalidURLException If the Url the method got from the id is invalid
     * @throws CouldNotExtractInfoException If the Video Information could not be extracted because YouTubes answer is
     * missing something important
     * @throws VideoNotFoundException If the given id is not an id of a YouTube Video
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static YoutubeVideoInterpretation getVideoInformation(String id)
            throws IOException, RequestException, InvalidURLException, CouldNotExtractInfoException, VideoNotFoundException, InternalError {
        JSONObject videoInfo = LinkHelper.sendRequest(
                UrlConstructor.getYTVideo()
                        .setId(id)
                        .build()
        );

        if (videoInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException("VideoId: " + id + " not found");
        }

        JSONObject items = videoInfo.getJSONArray("items").getJSONObject(0);
        if (items.isEmpty()) {
            throw new VideoNotFoundException(String.format("VideoId: %s could not be found", id));
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
     * @throws InvalidURLException If the URL that he got from the id is not valid
     * @throws IOException When the send command of the request failed
     * @throws RequestException If the request returned an invalid return code
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static long getVideoDuration(String id)
            throws InvalidURLException, IOException, RequestException, InternalError {
        try {
            return DurationCalc.ytStringToLong(LinkHelper.sendRequest(
                                    UrlConstructor.getYTVideoDuration()
                                            .setId(id)
                                            .build()
                            )
                            .getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONObject("contentDetails")
                            .getString("duration")
            );
        } catch (JSONException e) {
            return 0;
        }
    }

    private static boolean checkToken(JSONObject jsonObject) {
        try {
            jsonObject.get("nextPageToken");
        } catch(JSONException e) {
            return false;
        }
        return true;
    }

    public static YoutubePlaylistInterpretation getPlaylistInfoAsync(String id) {
        String playlistInfoUrl = UrlConstructor.getPlaylistInfo()
                .setId(id)
                .build();
        String playlistItemsUrl = UrlConstructor.getPlaylistItems()
                .setId(id)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();

        HttpRequest playlistInfoRequest = httpRequestBuilder
                .uri(URI.create(playlistInfoUrl))
                .GET()
                .build();
        HttpRequest playlistItemsRequest = httpRequestBuilder
                .uri(URI.create(playlistItemsUrl))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> playlistItems = httpClient.sendAsync(
                playlistItemsRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        CompletableFuture<HttpResponse<String>> playlistInfoFuture = httpClient.sendAsync(
                playlistInfoRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        JSONObject playlistInfo = new JSONObject(playlistInfoFuture.join().body());

        if (playlistInfo.isEmpty()) {
            throw new CouldNotExtractInfoException("PlaylistInfo is empty");
        }
        if (playlistInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException("PlaylistId: " + id + " could not be found");
        }

        JSONObject contentDetails;
        JSONObject snippet;
        try {
            contentDetails = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("ContentDetails was not found");
        }
        try {
            snippet = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("Snippet was not found");
        }

        String author = snippet.getString("channelTitle");
        if (author.contains("Topic")) {
            author = author.split(" - Topic")[0];
        }

        String title = snippet.getString("title");

        String thumbnail;
        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
        } else {
            thumbnail = null;
        }

        int itemCount = contentDetails.getInt("itemCount");

        long duration = 0;

        JSONObject playlistItemsObject = new JSONObject(playlistItems.join().body());
        System.out.println(playlistItemsObject);
        ArrayList<String> videoIds = new ArrayList<>();
        JSONArray array;
        ArrayList<CompletableFuture<Long>> responseList = new ArrayList<>();

        while (checkToken(playlistItemsObject)) {
            array = playlistItemsObject.getJSONArray("items");
            String url;

            try {
                url = UrlConstructor.getPlaylistItemsToken()
                        .setId(playlistItemsObject.getString("nextPageToken"))
                        .build();
            } catch (JSONException e) {
                return new YoutubePlaylistInterpretation(
                        duration,
                        author,
                        null,
                        title,
                        String.format(
                                "https://www.youtube.com/playlist=list=%s",
                                id
                        ),
                        thumbnail,
                        itemCount,
                        videoIds
                );
            }

            HttpRequest nextPageRequest = httpRequestBuilder
                    .uri(URI.create(url))
                    .build();

            CompletableFuture<JSONObject> nextPage = httpClient.sendAsync(
                    nextPageRequest,
                    HttpResponse.BodyHandlers.ofString()
            ).thenApply(YoutubeApi::toJSON);

            for (int i = 0; i < array.length(); i++) {
                String videoId;
                try {
                    videoId = array
                            .getJSONObject(i)
                            .getJSONObject("contentDetails")
                            .getString("videoId");
                } catch (JSONException e) {
                    continue;
                }

                videoIds.add(videoId);

                url = UrlConstructor.getYTVideoDuration()
                        .setId(videoId)
                        .build();

                HttpRequest videoDurationRequest = httpRequestBuilder
                        .uri(URI.create(url))
                        .GET()
                        .build();

                CompletableFuture<Long> videoDuration = httpClient.sendAsync(
                        videoDurationRequest,
                        HttpResponse.BodyHandlers.ofString()
                )
                        .thenApply(YoutubeApi::extractDuration)
                        .thenApply(DurationCalc::ytStringToLong);

                responseList.add(videoDuration);
            }

            playlistItemsObject = nextPage.join();
        }

        for (CompletableFuture<Long> singleDuration : responseList) {
            duration += singleDuration.join();
        }

        return new YoutubePlaylistInterpretation(
                duration,
                author,
                null,
                title,
                String.format(
                        "https://www.youtube.com/playlist=list=%s",
                        id
                ),
                thumbnail,
                itemCount,
                videoIds
        );
    }

    private static JSONObject toJSON(HttpResponse<String> response) {
        return new JSONObject(response.body());
    }

    private static String extractDuration(HttpResponse<String> videoResponse) {
        JSONObject toExtract = new JSONObject(videoResponse.body());
        try {
            return toExtract
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails")
                    .getString("duration");
        } catch (JSONException e) {
            return "PT0H0M0S";
        }
    }

    /**
     * The purpose of the method is to gather information about a playlist
     * @param id The YouTube id of the playlist
     * @return a YoutubePlaylistInterpretation Object that inhabits all important information
     * @throws InvalidURLException gets thrown if the Url it got from the id is invalid
     * @throws IOException When the send command of the request failed
     * @throws RequestException If the request  returned an invalid return code
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static YoutubePlaylistInterpretation getPlaylistInformation(String id)
            throws InvalidURLException, IOException, RequestException, InternalError {
        JSONObject playlistInfo = LinkHelper.sendRequest(
                UrlConstructor.getPlaylistInfo()
                        .setId(id)
                        .build()
        );
        JSONObject playlistItems = LinkHelper.sendRequest(
                UrlConstructor.getPlaylistItems()
                        .setId(id)
                        .build()
        );

        if (playlistInfo.isEmpty()) {
            throw new CouldNotExtractInfoException("PlaylistInfo is empty");
        }
        if (playlistInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException("PlaylistId: " + id + " could not be found");
        }

        JSONObject contentDetails;
        JSONObject snippet;
        try {
            contentDetails = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("ContentDetails was not found");
        }
        try {
            snippet = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("Snippet was not found");
        }

        String author = snippet.getString("channelTitle");
        if (author.contains("Topic")) {
            author = author.split(" - Topic")[0];
        }

        String title = snippet.getString("title");

        String thumbnail;
        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            thumbnail = snippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
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
                String videoId;
                try {
                    videoId = array
                            .getJSONObject(i)
                            .getJSONObject("contentDetails")
                            .getString("videoId");
                } catch (JSONException e) {
                    continue;
                }

                duration += getVideoDuration(videoId);
                videoIds.add(videoId);
            }
            playlistItemsObject = LinkHelper.sendRequest(
                    UrlConstructor.getPlaylistItemsToken()
                            .setId(id)
                            .setPageToken(playlistItemsObject.getString("nextPageToken"))
                            .build()
            );
        }

        return new YoutubePlaylistInterpretation(
                duration,
                author,
                null,
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
}
