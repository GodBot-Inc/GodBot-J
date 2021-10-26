package utils.linkProcessing.interpretations.youtube;

import utils.linkProcessing.interpretations.Interpretation;

import java.util.ArrayList;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation, Interpretation {

    private final long duration;
    private final String author;
    private final String title;
    private final String uri;
    private final String thumbnailUri;
    private final int size;
    private final ArrayList<YoutubeVideoInterpretation> videoInterpretations;

    public YoutubePlaylistInterpretation(long duration, String author, String title, String uri, String thumbnailUri, int size, ArrayList<YoutubeVideoInterpretation> videoInterpretations) {
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
        this.size = size;
        this.videoInterpretations = videoInterpretations;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getUrl() {
        return this.uri;
    }

    @Override
    public String getThumbnailUrl() {
        return this.thumbnailUri;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<YoutubeVideoInterpretation> getVideoInterpretations() {
        return this.videoInterpretations;
    }
}
