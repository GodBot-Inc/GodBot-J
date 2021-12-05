package com.godbot.utils.linkProcessing;

import com.godbot.discord.snippets.Keys;
import com.godbot.utils.Checks;
import com.godbot.utils.apis.youtube.YoutubeApi;
import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.LinkInterpretation.RequestException;
import com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfoException;
import com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFoundException;
import com.godbot.utils.interpretations.Interpretation;
import com.godbot.utils.interpretations.spotify.SpotifyInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import com.godbot.utils.logging.LinkProcessingLogger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

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
            case "youtube":
                try {
                    YoutubeInterpretation youtubeInterpretation = ytInterpret(url);
                    if (youtubeInterpretation instanceof YoutubeVideoInterpretation) {
                        interpretations.put(Keys.YTVIDEO, youtubeInterpretation);
                    } else {
                        interpretations.put(Keys.YTPLAYLIST, youtubeInterpretation);
                    }
                } catch(IOException | RequestException ignore) {}
                break;
            case "spotify":
                SpotifyInterpretation spotifyInterpretation = spotInterpret(url);
            default:
                throw new IllegalStateException("Unexpected value: " + platform);
        }
        return interpretations;
    }

    // YT
    private static HashMap<String, String> ytGetTypeAndId(String url) throws InvalidURLException {
        if (url.contains("list=")) {
            String id = url.split("list=")[0].split("&")[0];
            return new HashMap<>() {{
                put("type", "playlist");
                put("id", id);
            }};
        } else if (url.contains("watch?v=") && !url.contains("list=")) {
            String id = url.split("watch?v=")[0].split("&")[0];
            return new HashMap<>() {{
                put("type", "video");
                put("id", id);
            }};
        }
        throw new InvalidURLException(String.format("Could not fetch Type and Id of the given url %s", url));
    }

    private static YoutubeInterpretation ytInterpret(String url)
            throws InvalidURLException, IOException, RequestException, InternalError, CouldNotExtractInfoException, VideoNotFoundException {
        LinkProcessingLogger logger = getLogger();
        HashMap<String, String> typeAndId = ytGetTypeAndId(url);
        if (Objects.equals(typeAndId.get("type"), "playlist")) {
            return YoutubeApi.getPlaylistInformation(typeAndId.get("id"));
        } else if (Objects.equals(typeAndId.get("type"), "video")) {
            return YoutubeApi.getVideoInformation(typeAndId.get("id"));
        }
        throw new IllegalStateException("Unexpected value: " + typeAndId.get("type"));
    }
    // YT END

    // Spotify
    private static TypeAndId spotGetTypeAndId(String url)
            throws InvalidURLException {
        return new TypeAndId("moin", "moin");
    }

    private static SpotifyInterpretation spotInterpret(String url)
            throws InvalidURLException {
        LinkProcessingLogger logger = getLogger();
        TypeAndId typeAndId = spotGetTypeAndId(url);
        return new SpotifyInterpretation() {
            @Override
            public long getDuration() {
                return 0;
            }

            @Override
            public @NotNull String getCreator() {
                return null;
            }

            @Override
            public @NotNull String getCreatorLink() {
                return null;
            }

            @Override
            public @NotNull String getTitle() {
                return null;
            }

            @Override
            public @NotNull String getUrl() {
                return null;
            }

            @Override
            public @NotNull String getThumbnailUrl() {
                return null;
            }
        };
    }
    // Spotify END
}
