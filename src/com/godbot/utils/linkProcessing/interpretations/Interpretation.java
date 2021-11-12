package utils.linkProcessing.interpretations;

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
    String getAuthor();

    /**
     * @return the title of the song
     */
    String getTitle();

    /**
     * @return the url of the song
     */
    String getUrl();

    /**
     * @return the url of the thumbnail
     */
    String getThumbnailUrl();
}
