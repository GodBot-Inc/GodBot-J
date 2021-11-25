package utils.linkProcessing;

import discord.snippets.Keys;
import org.json.JSONObject;
import utils.apis.spotify.SpotifyApi;
import utils.apis.youtube.YoutubeApi;
import utils.customExceptions.LinkInterpretation.InvalidURLException;
import utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.customExceptions.LinkInterpretation.youtubeApi.CouldNotExtractInfoException;
import utils.customExceptions.LinkInterpretation.youtubeApi.VideoNotFoundException;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.Playable;
import utils.linkProcessing.interpretations.spotify.SpotifyInterpretation;
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
 * 2. Return Id or URL of the song given
 * 3. Check if the given video/playlist/album exists on another platform than the one given with the link
 * 4. Gather Information about the song from the platforms: yt, spotify, soundcloud and put it into an
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
            return false;
        } catch (MalformedURLException | URISyntaxException e) {
            return true;
        }
    }

    public static HashMap<String, Interpretation> interpret(String url)
            throws InvalidURLException, PlatformNotFoundException {
        if (isValid(url)) {
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
                SpotifyInterpretation spotifyInterpretation = spotInterpret(url);
            default:
                throw new IllegalStateException("Unexpected value: " + platform);
        }
        return interpretations;
    }

    /**
     *
     * @param url that should be processed
     * @return Playable which is passed to the Play Command
     * @throws PlatformNotFoundException If the platform of the link could not be determined
     */
    public static void getPlayable(String url)
            throws PlatformNotFoundException {
        String platform = getPlatform(url);
//        switch (platform) {
//            case "youtube":
//            case "spotify":
//            case "soundcloud":
//
//        }
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
        System.out.println(response);
        return new JSONObject(response.toString());
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
        // TODO Split the link so you get the type and id of the given thing
    }

    private static SpotifyInterpretation spotInterpret(String url)
            throws InvalidURLException {
        LinkProcessingLogger logger = getLogger();
        TypeAndId typeAndId = spotGetTypeAndId(url);
        // TODO Call spotify api to gather information about the song
    }
    // Spotify END
}
