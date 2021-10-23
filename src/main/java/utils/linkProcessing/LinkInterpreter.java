package utils.linkProcessing;

import utils.apis.youtube.youtubeApi;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.PlatformNotFound;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeInterpretation;
import utils.logging.LinkProcessingLogger;
import utils.logging.LoggerContent;

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

    private static boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static ArrayList<Interpretation> interpret(String url) throws InvalidURL, PlatformNotFound {
        if (!isValid(url)) {
            throw new InvalidURL(String.format("Url %s is not valid", url));
        }
        String platform = getPlatform(url);
        ArrayList<Interpretation> interpretations = new ArrayList<>();
        switch (platform) {
            case "youtube":
                ArrayList<YoutubeInterpretation> ytInterpretations = ytInterpret(url);
                break;
            case "spotify":
                throw new InvalidURL("Spotify is not supported yet");
            case "soundcloud":
                throw new InvalidURL("Soundcloud is not supported yet");
        }
        return new ArrayList<Interpretation>();
    }

    private static String getPlatform(String url) throws PlatformNotFound {
        if (url.contains("https://open.spotify.com/")) {
            return "spotify";
        } else if (url.contains("https://www.youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else if (url.contains("https://www.soundcloud.com/")) {
            return "soundcloud";
        } else {
            throw new PlatformNotFound(String.format("Platform for lin %s could not be determined", url));
        }
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

    private static ArrayList<YoutubeInterpretation> ytInterpret(String url) throws  InvalidURL {
        // TODO Send a request to youtube so you can get the song title / song titles for every song in a playlist
        LinkProcessingLogger logger = LinkProcessingLogger.getInstance();
        HashMap<String, String> typeAndId = ytGetTypeAndId(url);
        if (!typeAndId.containsKey("type")) {
            logger.warn(
                    new LoggerContent(
                        "warning",
                        "ytInterpret",
                        "Could not resolve key type",
                        new HashMap<String, String>() {{
                            put("url", url);
                            put("HashMap", typeAndId.toString());
                        }}
                    )
            );
        }
        if (!typeAndId.containsKey("id")) {
            logger.warn(
                    new LoggerContent(
                            "warning",
                            "ytInterpret",
                            "Could not resolve key id",
                            new HashMap<String, String>() {{
                                put("url", url);
                                put("HashMap", typeAndId.toString());
                            }}
                    )
            );
        }
        ArrayList<YoutubeInterpretation> ytInterpretation = new ArrayList<>();
        if (Objects.equals(typeAndId.get("type"), "playlist")) {
//            ytInterpretation.add(youtubeApi.getPlaylistInformation(typeAndId.get("id")));
        } else if (Objects.equals(typeAndId.get("type"), "video")) {
//            ytInterpretation.add(youtubeApi.getVideoInformation(typeAndId.get("id")));
        }
        return ytInterpretation;
    }
    // YT END
}
