package utils.linkProcessing.interpretations.soundcloud;

import utils.linkProcessing.interpretations.Interpretation;

public interface SoundCloudInterpretation extends Interpretation {
    /**
     * @return the link of the song/playlist/album author. It's SoundCloud exclusive because we only need
     * the SoundCloud Author Url.
     */
    String getAuthorUrl();
}
