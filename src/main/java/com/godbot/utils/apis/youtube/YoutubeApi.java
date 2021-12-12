package com.godbot.utils.apis.youtube;

import com.godbot.utils.audio.DurationCalc;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.requests.*;
import com.godbot.utils.customExceptions.ytApi.CouldNotExtractInfoException;
import com.godbot.utils.customExceptions.ytApi.QuotaExpired;
import com.godbot.utils.customExceptions.ytApi.VideoNotFoundException;
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
import java.util.concurrent.CompletableFuture;

public class YoutubeApi {

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
            throws IOException,
            RequestException,
            InvalidURLException,
            CouldNotExtractInfoException,
            VideoNotFoundException {

        JSONObject videoInfo = LinkHelper.sendRequest(
                UrlConstructor.getYTVideo()
                        .setId(id)
                        .build()
        );

        YoutubeVideoInterpretation.VideoBuilder builder =
                new YoutubeVideoInterpretation.VideoBuilder();

        if (videoInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException("VideoId: " + id + " not found");
        }

        JSONObject contentDetails;
        JSONObject snippet;
        JSONObject statistics;
        try {
            contentDetails = videoInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("ContentDetails was not found");
        }
        try {
            snippet = videoInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractInfoException("Snippet was not found");
        }
        try {
            statistics = videoInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("statistics");
        } catch (JSONException e) {
            throw new CouldNotExtractInfoException("Statistics were not found");
        }

        try {
            builder.setThumbnailUri(
                    snippet
                            .getJSONObject("thumbnails")
                            .getJSONObject("standard")
                            .getString("url")
            );
        } catch (JSONException e) {
            builder.setThumbnailUri(
                    snippet
                            .getJSONObject("thumbnails")
                            .getJSONObject("high")
                            .getString("url")
            );
        }

        if (snippet.getString("channelTitle").contains("- Topic")) {
            builder.setAuthor(
                    snippet
                            .getString("channelTitle")
                            .split(" - Topic")[0]
            );
        } else {
            builder.setAuthor(
                    snippet.getString("channelTitle")
            );
        }

        builder.setAuthorLink(String.format(
                "https://youtube.com/channel/%s",
                snippet.getString("channelId")
        ));
        builder.setTitle(snippet.getString("title"));
        builder.setViews(Integer.parseInt(statistics.getString("viewCount")));
        if (!statistics.getString("likeCount").equals("")) {
            builder.setLikes(Integer.parseInt(statistics.getString("likeCount")));
        }
        if (!statistics.getString("dislikeCount").equals("")) {
            builder.setDislikes(Integer.parseInt(statistics.getString("dislikeCount")));
        }
        if (!statistics.getString("commentCount").equals("")) {
            builder.setComments(Integer.parseInt(statistics.getString("commentCount")));
        }

        builder.setDuration(DurationCalc.ytStringToLong(contentDetails.getString("duration")));
        builder.setUri(
                String.format(
                        "https://youtube.com/watch?v=%s",
                        id
                )
        );
        builder.setMusicUri(
                String.format(
                        "https://music.youtube.com/watch?v=%s",
                        id
                )
        );

        return builder.build();
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

    public static String getFirst(String id) throws IOException, RequestException, InvalidURLException {
        String url = UrlConstructor.getPlaylistItems()
                .setId(id)
                .build();

        System.out.println(id);
        System.out.println(url);

        JSONObject playlistItems = LinkHelper.sendRequest(url);

        return String.format(
                UrlConstructor.getWatch().build(),
                playlistItems
                        .getJSONArray("items")
                        .getJSONObject(0)
                        .getJSONObject("contentDetails")
                        .getString("videoId")
        );
    }

    private static boolean checkToken(JSONObject jsonObject) {
        System.out.println(jsonObject);
        try {
            jsonObject.get("nextPageToken");
        } catch(JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Get Info about the yt video very fast
     * @param id of the playlist
     * @return YoutubePlaylistInterpretation, containing all info about the playlist
     * @throws QuotaExpired u shall not pass
     * @throws RateLimitException too much enthusiasm
     * @throws BadRequestException U fucked up
     * @throws InternalServerError yt fucked up
     * @throws EndpointMovedException moved
     * @throws NotFoundException nothing found
     */
    public static YoutubePlaylistInterpretation getPlaylistInfoAsync(String id)
            throws QuotaExpired,
            RateLimitException,
            BadRequestException,
            InternalServerError,
            EndpointMovedException,
            NotFoundException {
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

        CompletableFuture<HttpResponse<String>> playlistInfoFuture = httpClient.sendAsync(
                    playlistInfoRequest,
                    HttpResponse.BodyHandlers.ofString()
        );
        CompletableFuture<HttpResponse<String>> playlistItems = httpClient.sendAsync(
                playlistItemsRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        JSONObject playlistInfo = new JSONObject(playlistInfoFuture.join().body());

        YoutubePlaylistInterpretation.PlaylistBuilder interpretationBuilder =
                new YoutubePlaylistInterpretation.PlaylistBuilder();

        interpretationBuilder.setUri(
                String.format(
                        "https://www.youtube.com/playlist?list=%s",
                        id
                )
        );

        interpretationBuilder.setMusicUri(
                String.format(
                        "https://music.youtube.com/playlist?list=%s",
                        id
                )
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

        if (snippet.getString("channelTitle").contains(" - Topic")) {
            interpretationBuilder.setCreator(
                    snippet
                            .getString("channelTitle")
                            .split(" - Topic")[0]
            );
        } else {
            interpretationBuilder.setCreator(
                    snippet.getString("channelTitle")
            );
        }

        interpretationBuilder.setTitle(snippet.getString("title"));

        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            try {
                interpretationBuilder.setThumbnailUri(
                        snippet
                                .getJSONObject("thumbnails")
                                .getJSONObject("standard")
                                .getString("url")
                );
            } catch (JSONException e) {
                interpretationBuilder.setThumbnailUri(
                        snippet
                                .getJSONObject("thumbnails")
                                .getJSONObject("high")
                                .getString("url")
                );
            }
        }

        interpretationBuilder.setSize(contentDetails.getInt("itemCount"));

        JSONObject playlistItemsObject;
        playlistItemsObject = new JSONObject(playlistItems.join().body());
        System.out.println(playlistItemsObject);
        JSONArray array;
        ArrayList<CompletableFuture<Long>> responseList = new ArrayList<>();

        while (true) {
            System.out.println(playlistItemsObject);
            array = playlistItemsObject.getJSONArray("items");
            String url = null;

            boolean sendRequest;
            try {
                url = UrlConstructor.getPlaylistItemsToken()
                        .setId(id)
                        .setPageToken(playlistItemsObject.getString("nextPageToken"))
                        .build();
                sendRequest = true;
            } catch (JSONException e) {
                sendRequest = false;
            }

            CompletableFuture<JSONObject> nextPage = null;
            if (sendRequest) {
                HttpRequest nextPageRequest = httpRequestBuilder
                        .uri(URI.create(url))
                        .build();

                nextPage = httpClient.sendAsync(
                                nextPageRequest,
                                HttpResponse.BodyHandlers.ofString()
                        )
                        .thenApply(YoutubeApi::toJSON);
            }

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

                interpretationBuilder.addVideoId(videoId);

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

            if (nextPage == null) {
                break;
            }

            playlistItemsObject = nextPage.join();
        }

        for (CompletableFuture<Long> singleDuration : responseList) {
            try {
                interpretationBuilder.addDuration(singleDuration.join());
            } catch (RequestException ignore) {}
        }

        return interpretationBuilder.build();
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
}
