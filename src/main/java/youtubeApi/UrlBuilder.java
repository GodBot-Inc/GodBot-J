package youtubeApi;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;

/**
 * Link Builder is used to construct a link using . notation
 */
public class UrlBuilder {

    public static class UrlConstructor {
        @NotNull
        public static UrlBuilder getYTVideo() {
            return new UrlBuilder(
                    "https://youtube.googleapis.com/youtube/v3/" +
                            "videos?" +
                            "part=contentDetails" +
                            "&part=snippet" +
                            "&part=statistics" +
                            "&id=idHere" +
                            "&maxResults=1" +
                            "&key=apiKeyHere"
            );
        }

        @NotNull public static UrlBuilder getYTVideoDuration() {
            return new UrlBuilder(
                    "https://youtube.googleapis.com/youtube/v3/" +
                            "videos?" +
                            "part=contentDetails" +
                            "&id=idHere" +
                            "&maxResults=1" +
                            "&key=apiKeyHere"
            );
        }

        @NotNull public static UrlBuilder getPlaylistInfo() {
            return new UrlBuilder(
                    "https://youtube.googleapis.com/youtube/v3/" +
                            "playlists?" +
                            "part=snippet" +
                            "&part=contentDetails" +
                            "&id=idHere" +
                            "&maxResults=1" +
                            "&key=apiKeyHere"
            );
        }

        @NotNull public static UrlBuilder getPlaylistItems() {
            return new UrlBuilder(
                    "https://youtube.googleapis.com/youtube/v3/playlistItems?" +
                            "part=snippet" +
                            "&part=contentDetails" +
                            "&playlistId=playlistIdHere" +
                            "&key=apiKeyHere"
            );
        }

        @NotNull public static UrlBuilder getWatch() {
            return new UrlBuilder(
                    "https://youtube.com/watch?v=idHere"
            );
        }

        @NotNull public static UrlBuilder getSearch() { return new UrlBuilder(
                "https://youtube.googleapis.com/youtube/v3/search?part=sinppet" +
                        "&maxResults=5" +
                        "&q=searchHere" +
                        "&videoCategoryId=10" +
                        "&key=apiKeyHere"
        ); }

        @NotNull public static UrlBuilder getSearchWithChannel() { return new UrlBuilder(
                "https://youtube.googleapis.com/youtube/v3/search?part=sinppet" +
                        "&q=searchHere" +
                        "&channelId=channelIdHere" +
                        "&maxResults=5" +
                        "&videoCategoryId=10" +
                        "&key=apiKeyHere"
        ); }

        @NotNull public static UrlBuilder getPlaylistItemsToken() {
            return new UrlBuilder(
                    "https://youtube.googleapis.com/youtube/v3/" +
                            "playlistItems?" +
                            "part=snippet" +
                            "&part=contentDetails" +
                            "&pageToken=pageTokenHere" +
                            "&playlistId=playlistIdHere" +
                            "&key=apiKeyHere"
            );
        }
    }

    private final String url;
    private String pageToken;
    private String id;
    private String search;
    private String channelId;

    public UrlBuilder(String url) {
        this.url = url;
    }

    public UrlBuilder setPageToken(String pageToken) {
        this.pageToken = pageToken;
        return this;
    }

    public UrlBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UrlBuilder setSearch(String search) {
        this.search = search.replace(" ", "%20");
        return this;
    }

    public UrlBuilder setChannel(String channelId) {
        this.channelId = channelId;
        return this;
    }

    private static String getApiKey() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("YT_API_KEY");
    }

    public String build() {
        // TODO: You have to set all %s parameters at one String.format call
        try {
            String finalString = url;
            if (url.contains("q=searchHere")) {
                finalString = finalString.replace("searchHere", search);
            }
            if (url.contains("channelId=channelHere")) {
                finalString = finalString.replace("channelHere", channelId);
            }
            if (url.contains("pageToken=pageTokenHere")) {
                finalString = finalString.replace("pageTokenHere", pageToken);
            }
            if (url.contains("id=idHere")) {
                finalString = finalString.replace("idHere", id);
            }
            if (url.contains("playlistId=playlistIdHere")) {
                finalString = finalString.replace("playlistIdHere", id);
            }
            return finalString.replace("apiKeyHere", getApiKey());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
