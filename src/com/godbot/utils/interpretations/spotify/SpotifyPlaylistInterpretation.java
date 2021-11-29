package utils.interpretations.spotify;

import org.jetbrains.annotations.NotNull;

public class SpotifyPlaylistInterpretation implements SpotifyInterpretation{
    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public @NotNull String getCreator() {
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
