package utils.linkProcessing;

import utils.apis.youtube.youtubeApi;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.PlatformNotFound;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeInterpretation;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class LinkInterpreter {

    private static boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
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

    private static String[] ytGetTypeAndId(String url) throws InvalidURL {
        // TODO Determine which type of upload this is (video playlist)
        if (url.contains("list=")) {
            String id = url.split("list=")[0].split("&")[0];
            return new String[] {"playlist", id};
        } else if (url.contains("watch?v=") && !url.contains("list=")) {
            String id = url.split("watch?v=")[0].split("&")[0];
            return new String[] {"video", id};
        }
        throw new InvalidURL(String.format("Could not fetch Type and Id of the given url %s", url));
    }

    private static String ytGetSearchableFromUrl(String url) throws  InvalidURL {
        // TODO Send a request to youtube so you can get the song title / song titles for every song in a playlist
        String[] typeAndId = ytGetTypeAndId(url);
        return "";
    }

    public static ArrayList<Interpretation> interpret(String url) throws InvalidURL, PlatformNotFound {
        if (!isValid(url)) {
            throw new InvalidURL(String.format("Url %s is not valid", url));
        }
        String platform = getPlatform(url);
        String searchable;
        switch (platform) {
            case "youtube":
                // Here we get the searchable to search this song on spotify
                searchable = ytGetSearchableFromUrl(url);
                break;
            case "spotify":
                throw new InvalidURL("Spotify is not supported yet");
            case "soundcloud":
                throw new InvalidURL("Soundcloud is not supported yet");
        }
        return new ArrayList<Interpretation>();
    }
}
