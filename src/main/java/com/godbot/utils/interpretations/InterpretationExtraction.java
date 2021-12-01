package utils.interpretations;

import com.mongodb.lang.Nullable;
import discord.snippets.Keys;
import utils.interpretations.spotify.SpotifySongInterpretation;
import utils.interpretations.youtube.YoutubeVideoInterpretation;

import java.util.HashMap;

public class InterpretationExtraction {

    @Nullable public static Interpretation getFirstVideoInterpretation(HashMap<String, Interpretation> interpretations) {
        if (interpretations.isEmpty()) {
            return null;
        } else if (interpretations.containsKey(Keys.YTVIDEO)) {
            return interpretations.get(Keys.YTVIDEO);
        } else return interpretations.getOrDefault(Keys.SPOTSONG, null);
    }

    @Nullable public static YoutubeVideoInterpretation getYTVideoInterpretation(
            HashMap<String, Interpretation> interpretations
    ) {
        return (YoutubeVideoInterpretation) interpretations.getOrDefault(Keys.YTVIDEO, null);
    }

    public static SpotifySongInterpretation getSpotSongInterpretation(
            HashMap<String, Interpretation> interpretations
    ) {
        return (SpotifySongInterpretation) interpretations.getOrDefault(Keys.SPOTSONG, null);
    }
}
