package utils.linkProcessing.interpretations.youtube;

import utils.linkProcessing.interpretations.Interpretation;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation, Interpretation {

    public final long duration;
    public final String author;
    public final String title;
    public final String uri;
    public final String thumbnailUri;
    public final int size;

    public YoutubePlaylistInterpretation(long duration, String author, String title, String uri, String thumbnailUri, int size) {
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
        this.size = size;
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
}
