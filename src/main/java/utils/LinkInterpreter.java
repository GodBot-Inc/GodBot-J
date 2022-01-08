package utils;

import playableInfo.PlayableInfo;
import playableInfo.YouTubeSong;
import snippets.InterpretationKeys;
import youtubeApi.YoutubeApi;

import java.io.IOException;
import java.util.HashMap;

public class LinkInterpreter {
    public static HashMap<String, PlayableInfo> interpret(String url)
            throws InvalidURLException, PlatformNotFoundException {
        if (Checks.linkIsValid(url)) {
            throw new InvalidURLException();
        }
        String platform = LinkHelper.getPlatform(url);
        HashMap<String, PlayableInfo> interpretations = new HashMap<>();
        switch (platform) {
            case "youtube" -> {
                try {
                    PlayableInfo youtubeInterpretation = ytInterpret(url);
                    if (youtubeInterpretation instanceof YouTubeSong) {
                        interpretations.put(InterpretationKeys.YTVIDEO, youtubeInterpretation);
                    } else {
                        interpretations.put(InterpretationKeys.YTPLAYLIST, youtubeInterpretation);
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

    public static TypeAndId ytGetTypeAndId(String url) throws InvalidURLException {
        if (url.contains("list=")) {
            String id = url.split("list=")[1].split("&")[0];
            return new TypeAndId("playlist" , id);
        } else if (url.contains("watch?v=") && !url.contains("list=")) {
            String id = url.split("watch\\?v=")[1].split("&")[0];
            return new TypeAndId("video", id);
        }
        throw new InvalidURLException();
    }

    private static PlayableInfo ytInterpret(String url)
            throws InvalidURLException,
            IOException,
            RequestException,
            InternalError {
        TypeAndId typeAndId = ytGetTypeAndId(url);
        if (typeAndId.type.equals("playlist")) {
            return YoutubeApi.getPlaylistInfoAsync(typeAndId.Id);
        } else if (typeAndId.type.equals("video")) {
            return YoutubeApi.getVideoInformation(typeAndId.Id);
        }
        throw new IllegalStateException("Unexpected value: " + typeAndId.type);
    }
}
