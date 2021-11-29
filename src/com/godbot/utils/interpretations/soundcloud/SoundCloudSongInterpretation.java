package utils.interpretations.soundcloud;

import org.jetbrains.annotations.NotNull;

public class SoundCloudSongInterpretation implements SoundCloudInterpretation {
    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public @NotNull String getCreator() {
        return null;
    }

    public String getAuthorUrl() {
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
