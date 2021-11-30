package utils.apis.youtube;

import org.jetbrains.annotations.NotNull;
import utils.apis.youtube.LinkBuilder;

/**
 * LinkConstructor is just the class that sets the url and returns a LinkBuilder Instance.
 */
public class LinkConstructor {

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

    @NotNull public static LinkBuilder getYTVideo() {
        return new LinkBuilder(getVideoInformationUrl, false);
    }

    @NotNull public static LinkBuilder getYTVideoDuration() {
        return new LinkBuilder(getVideoInformation, false);
    }

    @NotNull public static LinkBuilder getPlaylistInfo() {
        return new LinkBuilder(getPlaylistInfoUrl, false);
    }

    @NotNull public static LinkBuilder getPlaylistItems() {
        return new LinkBuilder(getPlaylistItemsUrl, false);
    }

    @NotNull public static LinkBuilder getPlaylistItemsToken() {
        return new LinkBuilder(playlistItemsUrlWithToken, true);
    }
}
