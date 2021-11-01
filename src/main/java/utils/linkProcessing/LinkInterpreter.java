package utils.linkProcessing;

import okhttp3.Request;
import utils.apis.youtube.youtubeApi;
import utils.customExceptions.LinkInterpretation.InvalidPlatform;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.PlatformNotFound;
import utils.customExceptions.LinkInterpretation.RequestFailed;
import utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfo;
import utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFound;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;
import utils.logging.LinkProcessingLogger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/*
 * Procedure of the LinkInterpreter:
 * 1. Check the url (isValid, platform, type, etc.)
 * 2. If the url is not from soundcloud (which is the platform we stream from) we check if the given song
 * also exists on soundcloud.
 * 3. If so we get some song information into this format "artist - songtitle" and lavalink searches for the track
 * on soundcloud
 * (3.1) If the given link is a playlist we get the information for every song in the playlist and search for them all
 * NOTE: Use PlaylistItems to get the titles for every one
 * 4. Either we play the song if it was found or we don't if nothing was found
 */

/*
 * Procedure of the LinkInterpreter in the future
 * 1. Check url (isValid, platform, type etc.)
 * 2. Check if the given video/playlist/album exists on another platform than the one given with the link
 * 3. Gather Information about the song from the platforms: yt, spotify, soundcloud and put it into an
 * Interpretation object
 */

public class LinkInterpreter {
    private static LinkProcessingLogger getLogger() {
        return LinkProcessingLogger.getInstance();
    }

    // TODO Logging

    public static boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static HashMap<String, Interpretation> interpret(String url)
            throws InvalidURL, PlatformNotFound, InvalidPlatform {
        if (!isValid(url)) {
            throw new InvalidURL(String.format("Url %s is not valid", url));
        }
        String platform = getPlatform(url);
        HashMap<String, Interpretation> interpretations = new HashMap<>();
        switch (platform) {
            case "youtube":
                try {
                    YoutubeInterpretation youtubeInterpretation = ytInterpret(url);
                    if (youtubeInterpretation instanceof YoutubeVideoInterpretation) {
                        interpretations.put("YoutubeVideo", youtubeInterpretation);
                    } else {
                        interpretations.put("YoutubePlaylist", youtubeInterpretation);
                    }
                } catch(IOException | RequestFailed ignore) {}
                // TODO
                break;
            case "spotify":
                throw new InvalidPlatform("Spotify is not supported yet");
            case "soundcloud":
                throw new InvalidPlatform("Soundcloud is not supported yet");
            default:
                throw new IllegalStateException("Unexpected value: " + platform);
        }
        return interpretations;
    }

    public static String getPlatform(String url) throws PlatformNotFound {
        if (url.contains("https://open.spotify.com/")) {
            return "spotify";
        } else if (url.contains("https://youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else if (url.contains("https://soundcloud.com/")) {
            return "soundcloud";
        } else {
            throw new PlatformNotFound(String.format("Platform for lin %s could not be determined", url));
        }
    }

    public static String getSearchable(String platform, String url)
        throws InvalidPlatform, InvalidURL {
        // TODO write that function
        return switch (platform) {
            case "soundcloud" -> url;
            default -> "";
        };
    }

    // YT
    private static HashMap<String, String> ytGetTypeAndId(String url) throws InvalidURL {
        if (url.contains("list=")) {
            String id = url.split("list=")[0].split("&")[0];
            return new HashMap<String, String>() {{
                put("type", "playlist");
                put("id", id);
            }};
        } else if (url.contains("watch?v=") && !url.contains("list=")) {
            String id = url.split("watch?v=")[0].split("&")[0];
            return new HashMap<String, String>() {{
                put("type", "video");
                put("id", id);
            }};
        }
        throw new InvalidURL(String.format("Could not fetch Type and Id of the given url %s", url));
    }

    private static YoutubeInterpretation ytInterpret(String url)
            throws InvalidURL, IOException, RequestFailed, InternalError, CouldNotExtractInfo, VideoNotFound {
        // TODO Send a request to youtube so you can get the song title / song titles for every song in a playlist
        LinkProcessingLogger logger = getLogger();
        HashMap<String, String> typeAndId = ytGetTypeAndId(url);
        if (Objects.equals(typeAndId.get("type"), "playlist")) {
            return youtubeApi.getPlaylistInformation(typeAndId.get("id"));
        } else if (Objects.equals(typeAndId.get("type"), "video")) {
            try {
                // TODO Error handling
                return youtubeApi.getVideoInformation(typeAndId.get("id"));
            } catch(IOException | RequestFailed | InvalidURL e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("Unexpected value: " + typeAndId.get("type"));
    }
    // YT END
}
