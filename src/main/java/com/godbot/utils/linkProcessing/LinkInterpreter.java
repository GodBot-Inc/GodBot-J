package com.godbot.utils.linkProcessing;

import com.godbot.discord.snippets.Keys;
import com.godbot.utils.Checks;
import com.godbot.utils.apis.youtube.UrlConstructor;
import com.godbot.utils.apis.youtube.YoutubeApi;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.requests.RequestException;
import com.godbot.utils.customExceptions.ytApi.CouldNotExtractInfoException;
import com.godbot.utils.customExceptions.ytApi.VideoNotFoundException;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.youtube.YoutubeInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import com.godbot.utils.logging.LinkProcessingLogger;

import java.io.IOException;
import java.util.HashMap;

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
 * 2. Return Id or URL of the song given
 * 3. Check if the given video/playlist/album exists on another platform than the one given with the link
 * 4. Gather Information about the song from the platforms: yt, spotify, soundcloud and put it into an
 * Interpretation object
 */

public class LinkInterpreter {
    private static LinkProcessingLogger getLogger() {
        return LinkProcessingLogger.getInstance();
    }

    public static HashMap<String, Interpretation> interpret(String url)
            throws InvalidURLException, PlatformNotFoundException {
        if (Checks.linkIsValid(url)) {
            throw new InvalidURLException(String.format("Url %s is not valid", url));
        }
        String platform = LinkHelper.getPlatform(url);
        HashMap<String, Interpretation> interpretations = new HashMap<>();
        switch (platform) {
            case "youtube" -> {
                try {
                    YoutubeInterpretation youtubeInterpretation = ytInterpret(url);
                    if (youtubeInterpretation instanceof YoutubeVideoInterpretation) {
                        interpretations.put(Keys.YTVIDEO, youtubeInterpretation);
                    } else {
                        interpretations.put(Keys.YTPLAYLIST, youtubeInterpretation);
                    }
                } catch(IOException | RequestException ignore) {}
            }
//            case "spotify":
//                SpotifyInterpretation spotifyInterpretation = spotInterpret(url);
            default -> {
                throw new IllegalStateException("Unexpected value: " + platform);
            }
        }
        return interpretations;
    }

    public static String getFirst(String url)
            throws PlatformNotFoundException, InvalidURLException, IOException, RequestException {
        String platform = LinkHelper.getPlatform(url);
        if (platform.equals("youtube")) {
            TypeAndId typeAndId = ytGetTypeAndId(url);
            if (typeAndId.type.equals("playlist")) {
                return YoutubeApi.getFirst(typeAndId.Id);
            }
            return UrlConstructor.getWatch().setId(typeAndId.Id).build();
        }
        throw new InvalidURLException("Url " + url + " could not be determined");
    }

    // YT
    public static TypeAndId ytGetTypeAndId(String url) throws InvalidURLException {
        if (url.contains("list=")) {
            System.out.println("url contains list=");
            String id = url.split("list=")[1].split("&")[0];
            System.out.println("p id gotten");
            return new TypeAndId("playlist" , id);
        } else if (url.contains("watch?v=") && !url.contains("list=")) {
            System.out.println("contains watch?v=");
            String id = url.split("watch\\?v=")[1].split("&")[0];
            System.out.println("v id gotten");
            return new TypeAndId("video", id);
        }
        throw new InvalidURLException(String.format("Could not fetch Type and Id of the given url %s", url));
    }

    private static YoutubeInterpretation ytInterpret(String url)
            throws InvalidURLException, IOException, RequestException, InternalError, CouldNotExtractInfoException, VideoNotFoundException {
        TypeAndId typeAndId = ytGetTypeAndId(url);
        if (typeAndId.type.equals("playlist")) {
            return YoutubeApi.getPlaylistInfoAsync(typeAndId.Id);
        } else if (typeAndId.type.equals("video")) {
            return YoutubeApi.getVideoInformation(typeAndId.Id);
        }
        throw new IllegalStateException("Unexpected value: " + typeAndId.type);
    }
    // YT END

    // Spotify
//    private static TypeAndId spotGetTypeAndId(String url)
//            throws InvalidURLException {
//        return new TypeAndId("moin", "moin");
//    }
//
//    private static SpotifyInterpretation spotInterpret(String url)
//            throws InvalidURLException {
//        TypeAndId typeAndId = spotGetTypeAndId(url);
//    }
    // Spotify END
}
