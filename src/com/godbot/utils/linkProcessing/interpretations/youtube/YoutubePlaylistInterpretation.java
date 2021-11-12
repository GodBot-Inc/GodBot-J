package utils.linkProcessing.interpretations.youtube;

import java.util.ArrayList;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation {

    private final long duration;
    private final String author;
    private final String title;
    private final String uri;
    private final String thumbnailUri;
    private final int size;
    private final ArrayList<String> videoIds;

    public YoutubePlaylistInterpretation(long duration, String author, String title, String uri, String thumbnailUri, int size, ArrayList<String> videoIds) {
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
        this.size = size;
        this.videoIds = videoIds;
    }

    @Override
    public String toString() {
        return String.format(
                "YoutubePlaylistInterpretation=[duration=%s, author=%s, title=%s, uri=%s, thumbnailUri=%s, " +
                        "size=%s, videoIds=%s]",
                duration,
                author,
                title,
                uri,
                thumbnailUri,
                size,
                videoIds.toString()
        );
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

    public ArrayList<String> getVideoIds() {
        return this.videoIds;
    }
}
