package utils.audio.linkProcessing;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public interface LinkInterpretation {

    String getSearchTerm();

    // Returns true if the searched song can be found on spotify / soundcloud etc.
    boolean isProvidable();

    // Returns the duration of the song in milliseconds
    int getDuration();

    // Track Data are things like: Title, Author, playlist, album, collection etc. (get creative)
    String getTrackData();
}
