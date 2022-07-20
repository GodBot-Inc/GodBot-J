package youtubeApi;

import ktUtils.CouldNotExtractVideoInformation;
import ktUtils.RequestException;
import ktUtils.VideoNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import playableInfo.YouTubePlaylist;
import playableInfo.YouTubeSong;
import utils.DurationCalc;
import utils.LinkHelper;
import ktUtils.UrlBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class YoutubeApi {

    public static void searchLogic(String url)
            throws IOException, RequestException {
        JSONObject searchResult = LinkHelper.sendRequest(url);
        System.out.println(searchResult.toString(4));
    }

    /**
     * This function gathers information about the video with the given id
     * @param id The YouTube id of the video that should be gathered information about
     * @return Returns a YoutubeVideoInterpretation Object which contains all the gathered information
     * @throws IOException If the request sending failed
     * @throws RequestException If the request returns an invalid response code
     * @throws CouldNotExtractVideoInformation If the Video Information could not be extracted because YouTubes answer is
     * missing something important
     * @throws VideoNotFoundException If the given id is not an id of a YouTube Video
     * @throws InternalError If YouTube has issues resolving the request
     */
    public static YouTubeSong getVideoInformation(String id)
            throws IOException,
            RequestException,
            CouldNotExtractVideoInformation,
            VideoNotFoundException {

        JSONObject videoInfo = LinkHelper.sendRequest(
                new UrlBuilder.YT().getVideo()
                        .id(id)
                        .buildString()
        );

        YouTubeSong.Builder builder = new YouTubeSong.Builder();

        if (videoInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException();
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
            throw new CouldNotExtractVideoInformation();
        }
        try {
            snippet = videoInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractVideoInformation();
        }
        try {
            statistics = videoInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("statistics");
        } catch (JSONException e) {
            throw new CouldNotExtractVideoInformation();
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
            builder.setCreator(
                    snippet
                            .getString("channelTitle")
                            .split(" - Topic")[0]
            );
        } else {
            builder.setCreator(
                    snippet.getString("channelTitle")
            );
        }

        builder.setCreatorLink(String.format(
                "https://youtube.com/channel/%s",
                snippet.getString("channelId")
        ));
        builder.title(snippet.getString("title"))
                .views(Integer.parseInt(statistics.getString("viewCount")));
        if (!statistics.getString("likeCount").equals("")) {
            builder.setLikes(Integer.parseInt(statistics.getString("likeCount")));
        }
        if (!statistics.getString("commentCount").equals("")) {
            builder.setComments(Integer.parseInt(statistics.getString("commentCount")));
        }

        builder.duration(DurationCalc.ytStringToLong(contentDetails.getString("duration")))
                .uri("https://youtube.com/watch?v=" + id)
                .songId(id);

        return builder.build();
    }

    /**
     * Get Info about the yt video very fast
     * @param id of the playlist
     * @return YoutubePlaylistInterpretation, containing all info about the playlist
     * @throws VideoNotFoundException If the video was simply not found
     * @throws CouldNotExtractVideoInformation if YouTube delivered something we can not work with
     */
    public static YouTubePlaylist getPlaylistInfoAsync(String id)
            throws VideoNotFoundException, CouldNotExtractVideoInformation {
        String playlistInfoUrl = new UrlBuilder.YT().getPlaylist()
                .id(id)
                .buildString();
        String playlistItemsUrl = new UrlBuilder.YT().getPlaylistItems()
                .id(id)
                .buildString();

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

        YouTubePlaylist.Builder playableInfoBuilder = new YouTubePlaylist.Builder();

        playableInfoBuilder.uri("https://www.youtube.com/playlist?list=" + id);

        if (playlistInfo.isEmpty()) {
            throw new CouldNotExtractVideoInformation();
        }
        if (playlistInfo.getJSONArray("items").isEmpty()) {
            throw new VideoNotFoundException();
        }

        JSONObject contentDetails;
        JSONObject snippet;
        try {
            contentDetails = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("contentDetails");
        }  catch (JSONException e) {
            throw new CouldNotExtractVideoInformation();
        }
        try {
            snippet = playlistInfo
                    .getJSONArray("items")
                    .getJSONObject(0)
                    .getJSONObject("snippet");
        }  catch (JSONException e) {
            throw new CouldNotExtractVideoInformation();
        }

        if (snippet.getString("channelTitle").contains(" - Topic")) {
            playableInfoBuilder.creator(snippet.getString("channelTitle").split(" - Topic")[0]);
        } else {
            playableInfoBuilder.creator(snippet.getString("channelTitle"));
        }

        playableInfoBuilder.title(snippet.getString("title"));

        if (!snippet.getJSONObject("thumbnails").isEmpty()) {
            try {
                playableInfoBuilder.thumbnailUri(
                        snippet
                                .getJSONObject("thumbnails")
                                .getJSONObject("standard")
                                .getString("url")
                );
            } catch (JSONException e) {
                playableInfoBuilder.thumbnailUri(
                        snippet
                                .getJSONObject("thumbnails")
                                .getJSONObject("high")
                                .getString("url")
                );
            }
        }

        int size = contentDetails.getInt("itemCount");
        playableInfoBuilder.setSize(size);

        JSONObject playlistItemsObject;
        playlistItemsObject = new JSONObject(playlistItems.join().body());
        JSONArray array;
        ArrayList<Future<YouTubeSong>> ytSongList = new ArrayList<>();
        int count = 0;

        while (true) {
            array = playlistItemsObject.getJSONArray("items");
            String url = null;

            boolean sendRequest;
            try {
                url = new UrlBuilder.YT().getPlaylistItemsToken()
                        .id(id)
                        .pageToken(playlistItemsObject.getString("nextPageToken"))
                        .buildString();
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
                count++;
                String videoId;
                try {
                    videoId = array
                            .getJSONObject(i)
                            .getJSONObject("contentDetails")
                            .getString("videoId");
                } catch (JSONException e) {
                    continue;
                }

                playableInfoBuilder.addVideoId(videoId);

                ytSongList.add(Executors.newCachedThreadPool().submit(() -> getVideoInformation(videoId)));
            }

            if (nextPage == null) {
                break;
            }
            if (count >= size) {
                break;
            }

            playlistItemsObject = nextPage.join();
        }

        for (Future<YouTubeSong> ytSong : ytSongList) {
            try {
                YouTubeSong cur = ytSong.get();
                playableInfoBuilder.addDuration(cur.getDuration());
            } catch (ExecutionException | InterruptedException ignore) {}
        }

        return playableInfoBuilder.build();
    }

    private static JSONObject toJSON(HttpResponse<String> response) {
        return new JSONObject(response.body());
    }

}
