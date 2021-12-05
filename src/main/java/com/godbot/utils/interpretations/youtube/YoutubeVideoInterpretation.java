package com.godbot.utils.interpretations.youtube;

import org.jetbrains.annotations.NotNull;

public class YoutubeVideoInterpretation implements YoutubeInterpretation {

    private final long duration;
    private final String author;
    private final String authorLink;
    private final String title;
    private final String uri;
    private final String thumbnailUri;
    private final long likes;
    private final long dislikes;
    private final long views;
    private final long comments;

    public YoutubeVideoInterpretation(
            long duration,
            String author,
            String authorLInk,
            String title,
            String uri,
            String thumbnailUri,
            long likes,
            long dislikes,
            long views,
            long comments
    ) {
        this.duration = duration;
        this.author = author;
        this.authorLink = authorLInk;
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return String.format(
                "YoutubeVideoInterpretation=[duration=%s, author=%s, title=%s, uri=%s, " +
                        "thumbnailUri=%s, likes=%s, dislikes=%s, views=%s, comments=%s]",
                duration,
                author,
                title,
                uri,
                thumbnailUri,
                likes,
                dislikes,
                views,
                comments
        );
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public @NotNull String getCreator() {
        return this.author;
    }

    @NotNull public String getCreatorLink() {
        return this.authorLink;
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
