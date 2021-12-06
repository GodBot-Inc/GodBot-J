package com.godbot.utils.interpretations;

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
    @Nullable String getCreator();

    /**
     * @return The link to the author of the song
     */
    @Nullable String getCreatorLink();

    /**
     * @return the title of the song
     */
    @Nullable String getTitle();

    /**
     * @return the url of the song
     */
    @Nullable String getUrl();

    /**
     * @return the url of the thumbnail
     */
    @Nullable String getThumbnailUrl();
}
