package utils.linkProcessing;

import org.jetbrains.annotations.NotNull;
import utils.customExceptions.InvalidPlatform;
import utils.customExceptions.InvalidURL;
import utils.linkProcessing.interpretations.Interpretation;
import utils.linkProcessing.interpretations.SpotifyInterpretation;

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

    private static String getPlatform(String url) throws InvalidURL {
        if (url.contains("https://open.spotify.com/")) {
            return "spotify";
        } else if (url.contains("https://youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else if (url.contains("https://soundcloud.com/")) {
            return "soundcloud";
        } else {
            return null;
        }
    }

    public static ArrayList<Interpretation> interpret(String url) throws InvalidURL, InvalidPlatform {
        if (!isValid(url)) {
            throw new InvalidURL(String.format("Url %s is not valid", url));
        }
        String platform = getPlatform(url);
        if (Objects.equals(platform, "soundcloud") || Objects.equals(platform, "spotify")) {
            throw new InvalidPlatform("Soundcloud and Spotify urls are not supported yet");
        }
        return new ArrayList<Interpretation>();
    }
}
