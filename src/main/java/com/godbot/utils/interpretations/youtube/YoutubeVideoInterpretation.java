package com.godbot.utils.interpretations.youtube;

import org.jetbrains.annotations.Nullable;

public class YoutubeVideoInterpretation implements YoutubeInterpretation {

    public static class VideoBuilder {

        private long duration = 0;
        private String author;
        private String authorLink;
        private String title;
        private String uri;
        private String musicUri;
        private String thumbnailUri;
        private long likes = 0;
        private long dislikes = 0;
        private long views = 0;
        private long comments = 0;


        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setAuthorLink(String authorLink) {
            this.authorLink = authorLink;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public void setLikes(long likes) {
            this.likes = likes;
        }

        public void setDislikes(long dislikes) {
            this.dislikes = dislikes;
        }

        public void setViews(long views) {
            this.views = views;
        }

        public void setComments(long comments) {
            this.comments = comments;
        }

        public void setMusicUri(String uri) {
            this.musicUri = uri;
        }

        public YoutubeVideoInterpretation build() {
            return new YoutubeVideoInterpretation(
                    duration,
                    author,
                    authorLink,
                    title,
                    uri,
                    musicUri,
                    thumbnailUri,
                    likes,
                    dislikes,
                    views,
                    comments
            );
        }
    }

    private final long duration;
    private final String author;
    private final String authorLink;
    private final String title;
    private final String uri;
    private final String musicUri;
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
            String musicUri,
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
        this.musicUri = musicUri;
        this.thumbnailUri = thumbnailUri;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return String.format(
                "YoutubeVideoInterpretation=[duration=%s, author=%s, title=%s, uri=%s, musicUri=%s, " +
                        "thumbnailUri=%s, likes=%s, dislikes=%s, views=%s, comments=%s]",
                duration,
                author,
                title,
                uri,
                musicUri,
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
    public @Nullable String getCreator() {
        return this.author;
    }

    @Nullable public String getCreatorLink() {
        return this.authorLink;
    }

    @Override
    public @Nullable String getTitle() {
        return this.title;
    }

    @Override
    public @Nullable String getUrl() {
        return this.uri;
    }

    public @Nullable String getMusicUrl() {
        return this.musicUri;
    }

    @Override
    public @Nullable String getThumbnailUrl() {
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
