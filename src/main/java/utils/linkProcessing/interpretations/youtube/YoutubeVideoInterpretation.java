package utils.linkProcessing.interpretations.youtube;

import utils.linkProcessing.interpretations.Interpretation;

public class YoutubeVideoInterpretation implements YoutubeInterpretation, Interpretation {

    public final long duration;
    public final String author;
    public final String title;
    public final String uri;
    public final String thumbnailUri;
    public final long likes;
    public final long dislikes;
    public final long views;
    public final long comments;

    public YoutubeVideoInterpretation(long duration, String author, String title, String uri, String thumbnailUri, long likes, long dislikes, long views, long comments) {
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.comments = comments;
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

    public long getLikes() {
        return likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public long getViews() {
        return views;
    }

    public long getComments() {
        return comments;
    }
}
