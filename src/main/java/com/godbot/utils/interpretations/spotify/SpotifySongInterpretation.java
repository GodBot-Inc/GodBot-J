package com.godbot.utils.interpretations.spotify;

import com.godbot.utils.interpretations.Interpretation;
import org.jetbrains.annotations.NotNull;

public class SpotifySongInterpretation implements Interpretation {
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
}
