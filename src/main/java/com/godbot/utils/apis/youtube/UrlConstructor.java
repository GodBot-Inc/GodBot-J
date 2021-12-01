package utils.apis.youtube;

import org.jetbrains.annotations.NotNull;

/**
 * LinkConstructor is just the class that sets the url and returns a LinkBuilder Instance.
 */
public class UrlConstructor {

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

    private static final String getVideoInformation = "https://youtube.googleapis.com/youtube/v3/" +
            "videos?" +
            "part=contentDetails" +
            "&id=%s" +
            "&maxResults=1" +
            "&key=%s";

    @NotNull public static UrlBuilder getYTVideo() {
        return new UrlBuilder(getVideoInformationUrl, false);
    }

    @NotNull public static UrlBuilder getYTVideoDuration() {
        return new UrlBuilder(getVideoInformation, false);
    }

    @NotNull public static UrlBuilder getPlaylistInfo() {
        return new UrlBuilder(getPlaylistInfoUrl, false);
    }

    @NotNull public static UrlBuilder getPlaylistItems() {
        return new UrlBuilder(getPlaylistItemsUrl, false);
    }

    @NotNull public static UrlBuilder getPlaylistItemsToken() {
        return new UrlBuilder(playlistItemsUrlWithToken, true);
    }
}
