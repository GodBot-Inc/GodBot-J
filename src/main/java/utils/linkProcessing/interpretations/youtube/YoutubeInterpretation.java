package utils.linkProcessing.interpretations.youtube;

import utils.linkProcessing.interpretations.Interpretation;

/**
 * Wrapper for all YoutubeInterpretations
 */
public interface YoutubeInterpretation extends Interpretation {

    /**
     * @return the duration of the song in milliseconds
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
