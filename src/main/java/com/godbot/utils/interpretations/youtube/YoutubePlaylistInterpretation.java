package utils.interpretations.youtube;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation {

    private final long duration;
    private final String creator;
    private final String creatorUri;
    private final String title;
    private final String uri;
    private final String thumbnailUri;
    private final int size;
    private final ArrayList<String> videoIds;

    public YoutubePlaylistInterpretation(
            long duration,
            String creator,
            String creatorUri,
            String title,
            String uri,
            String thumbnailUri,
            int size,
            ArrayList<String> videoIds
    ) {
        this.duration = duration;
        this.creator = creator;
        this.creatorUri = creatorUri;
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
                creator,
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
    public @NotNull String getCreator() {
        return this.creator;
    }

    @Override
    public @NotNull String getCreatorLink() {
        return this.creatorUri;
    }

    @Override
    public @NotNull String getTitle() {
        return this.title;
    }

    @Override
    public @NotNull String getUrl() {
        return this.uri;
    }

    @Override
    public @NotNull String getThumbnailUrl() {
        return this.thumbnailUri;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<String> getVideoIds() {
        return this.videoIds;
    }
}
