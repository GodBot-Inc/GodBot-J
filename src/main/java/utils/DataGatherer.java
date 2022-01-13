package utils;

import ktUtils.InvalidURLException;
import ktUtils.PlatformNotFoundException;
import ktUtils.RequestException;
import net.dv8tion.jda.api.entities.Member;
import playableInfo.PlayableInfo;
import youtubeApi.YoutubeApi;

import java.io.IOException;

public class DataGatherer {
    public static PlayableInfo gatherPlayableInfo(String url, Member requester)
            throws InvalidURLException, PlatformNotFoundException, InternalError {
        if (Checks.linkIsValid(url)) {
            throw new InvalidURLException();
        }
        String platform = LinkHelper.getPlatform(url);
        switch (platform) {
            case "youtube" -> {
                try {
                    return gatherYTData(url, requester);
                } catch(IOException | RequestException ignore) {}
            }
        }
        throw new IllegalStateException("Data Gatherer Unexpected value: " + platform);
    }

    public static TypeAndId ytGetTypeAndId(String url) throws InvalidURLException {
        // YouTube share url of videos
        if (url.contains("youtu.be/")) {
            return new TypeAndId("video", url.split(".be/")[1]);
        }
        // If it's a playlist link
        if (url.contains("list=")) {
            return new TypeAndId("playlist" , url.split("list=")[1].split("&")[0]);
        }
        // If it's a video link, but not a playlist url
        else if (url.contains("watch?v=") && !url.contains("list=")) {
            return new TypeAndId("video", url.split("watch\\?v=")[1].split("&")[0]);
        }
        // The Url is invalid, since nothing could be extracted
        throw new InvalidURLException();
    }

    private static PlayableInfo gatherYTData(String url, Member requester)
            throws InvalidURLException,
            IOException,
            RequestException,
            InternalError {
        TypeAndId typeAndId = ytGetTypeAndId(url);
        if (typeAndId.type.equals("playlist")) {
            return YoutubeApi.getPlaylistInfoAsync(typeAndId.Id, requester);
        } else if (typeAndId.type.equals("video")) {
            return YoutubeApi.getVideoInformation(typeAndId.Id, requester);
        }
        throw new IllegalStateException("Unexpected value: " + typeAndId.type);
    }
}
