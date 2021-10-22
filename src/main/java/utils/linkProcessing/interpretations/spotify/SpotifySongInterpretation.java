package utils.linkProcessing.interpretations.spotify;

import utils.linkProcessing.interpretations.Interpretation;

public class SpotifySongInterpretation implements Interpretation {

    public final String searchTerm;
    public final long duration;
    public final String author;
    public final String title;
    public final String album;
    public final String uri;
    public final String type;

    public SpotifySongInterpretation(String searchTerm, long duration, String author, String title, String album, String uri, String type) {
        this.searchTerm = searchTerm;
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.album = album;
        this.uri = uri;
        this.type = type;
    }
}

// TODO Find a solution for quick button communication (f.e.: short questions, Voting systems for commands like skip, play etc.) -> redis
