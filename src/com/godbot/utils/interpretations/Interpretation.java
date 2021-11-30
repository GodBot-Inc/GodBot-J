package utils.interpretations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 Wrapper interface for all Interpretation types
 */
public interface Interpretation {
    /**
     * @return the duration of the song/s in milliseconds
     */
    long getDuration();

    /**
     * @return the author of the song
     */
    @NotNull String getCreator();

    /**
     * @return The link to the author of the song
     */
    @Nullable String getCreatorLink();

    /**
     * @return the title of the song
     */
    @NotNull String getTitle();

    /**
     * @return the url of the song
     */
    @NotNull String getUrl();

    /**
     * @return the url of the thumbnail
     */
    @NotNull String getThumbnailUrl();
}
