package utils.interpretations.spotify;

import org.jetbrains.annotations.NotNull;
import utils.interpretations.Interpretation;

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
