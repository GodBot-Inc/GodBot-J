package com.godbot.utils.interpretations;

import com.godbot.discord.snippets.Keys;
import com.godbot.utils.interpretations.spotify.SpotifySongInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;
import com.godbot.utils.interpretations.youtube.YoutubeVideoInterpretation;
import com.mongodb.lang.Nullable;

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

    @Nullable public static YoutubePlaylistInterpretation getYTPlaylistInterpretation(
            HashMap<String, Interpretation> interpretations
    ) {
        return (YoutubePlaylistInterpretation) interpretations.getOrDefault(Keys.YTPLAYLIST, null);
    }

    public static SpotifySongInterpretation getSpotSongInterpretation(
            HashMap<String, Interpretation> interpretations
    ) {
        return (SpotifySongInterpretation) interpretations.getOrDefault(Keys.SPOTSONG, null);
    }
}
