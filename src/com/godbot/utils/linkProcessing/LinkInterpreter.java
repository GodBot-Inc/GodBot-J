package utils.linkProcessing;

import discord.snippets.Keys;
import org.json.JSONObject;
import utils.apis.youtube.YoutubeApi;
import utils.customExceptions.LinkInterpretation.InvalidPlatformException;
import utils.customExceptions.LinkInterpretation.InvalidURLException;
import utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfoException;
import utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFoundException;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.soundcloud.SoundCloudInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;
import utils.logging.LinkProcessingLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
            throws InvalidURLException, PlatformNotFoundException {
        if (!isValid(url)) {
            throw new InvalidURLException(String.format("Url %s is not valid", url));
        }
        String platform = getPlatform(url);
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
            case "soundcloud":
            default:
                throw new IllegalStateException("Unexpected value: " + platform);
        }
        return interpretations;
    }

    public static String convertToSoundCloud(String url)
            throws
            PlatformNotFoundException,
            InvalidURLException,
            VideoNotFoundException,
            InternalError,
            IOException,
            IllegalStateException,
            RequestException,
            InvalidPlatformException {
        String platform = getPlatform(url);
        return switch (platform) {
            case "soundcloud" -> url;
            case "spotify" -> {
//                String[] titleAndAuthor = SpotifyApi.getTitleAndAuthor(url);
                throw new InvalidPlatformException("Spotify is not supported yet");
            }
            case "youtube" -> {
//                String[] titleAndAuthor = YoutubeApi.getTitleAndAuthor(url);
                throw new InvalidPlatformException("YouTube is not supported yet");
            }
            default -> throw new IllegalStateException("Unexpected Value: " + platform);
        };
    }

    /**
     * Just a helper function to get info
     * @param url that the platform should be determined of
     * @return the platform
     * @throws PlatformNotFoundException if no platform could be found
     */
    public static String getPlatform(String url) throws PlatformNotFoundException {
        if (url.contains("https://open.spotify.com/")) {
            return "spotify";
        } else if (url.contains("https://youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else if (url.contains("https://soundcloud.com/")) {
            return "soundcloud";
        } else {
            throw new PlatformNotFoundException(String.format("Platform for lin %s could not be determined", url));
        }
    }

    /**
     * A method that is there for sending requests
     * @param url The destination that a request will be sent to
     * @return the response of the website as JSONObject
     * @throws IOException If the request failed
     * @throws RequestException If the request returned an invalid return code
     * @throws InvalidURLException If the passed URL is invalid
     * @throws InternalError If the website has trouble processing the request
     */
    public static JSONObject sendRequest(String url)
            throws IOException, RequestException, InvalidURLException, InternalError {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        switch (responseCode) {
            case 400, 401, 403 -> throw new RequestException("The request that was sent failed");
            case 404 -> throw new InvalidURLException("The request returned a 404 error");
            case 500, 501, 502, 503, 504 -> throw new InternalError("The site has some issues resolving your request");
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return new JSONObject(response.toString());
    }

    // YT
    private static HashMap<String, String> ytGetTypeAndId(String url) throws InvalidURLException {
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
    // Spotify END

    // SoundCloud
    private static SoundCloudInterpretation scInterpret(String url)
        throws IOException, RequestException, InvalidURLException, InternalError {
        return new SoundCloudInterpretation() {
            @Override
            public long getDuration() {
                return 0;
            }

            @Override
            public String getAuthor() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getUrl() {
                return null;
            }

            @Override
            public String getAuthorUrl() {
                return null;
            }

            @Override
            public String getThumbnailUrl() {
                return null;
            }
        };
    }
    // SoundCloud END
}
