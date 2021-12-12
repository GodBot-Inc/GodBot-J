package com.godbot.utils.interpretations.youtube;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation {

    public static class PlaylistBuilder {

        private long duration = 0;
        private String creator;
        private String creatorUri;
        private String title;
        private String uri;
        private String musicUri;
        private String thumbnailUri;
        private int size = 0;
        private ArrayList<String> videoIds = new ArrayList<>();

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void addDuration(long duration) {
            this.duration += duration;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public void setCreatorUri(String creatorUri) {
            this.creatorUri = creatorUri;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public void setMusicUri(String uri) {
            this.musicUri = uri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public void setVideoIds(ArrayList<String> videoIds) {
            this.videoIds = videoIds;
        }

        public void addVideoId(String videoId) {
            this.videoIds.add(videoId);
        }

        public YoutubePlaylistInterpretation build() {
            return new YoutubePlaylistInterpretation(
                    duration,
                    creator,
                    creatorUri,
                    title,
                    uri,
                    musicUri,
                    thumbnailUri,
                    size,
                    videoIds
            );
        }
    }

    private final long duration;
    private final String creator;
    private final String creatorUri;
    private final String title;
    private final String uri;
    private final String musicUri;
    private final String thumbnailUri;
    private final int size;
    private final ArrayList<String> videoIds;

    public YoutubePlaylistInterpretation(
            long duration,
            String creator,
            String creatorUri,
            String title,
            String uri,
            String musicUri,
            String thumbnailUri,
            int size,
            ArrayList<String> videoIds
    ) {
        this.duration = duration;
        this.creator = creator;
        this.creatorUri = creatorUri;
        this.title = title;
        this.uri = uri;
        this.musicUri = musicUri;
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
    public @Nullable String getCreator() {
        return this.creator;
    }

    @Override
    public @Nullable String getCreatorLink() {
        return this.creatorUri;
    }

    @Override
    public @Nullable String getTitle() {
        return this.title;
    }

    @Override
    public @Nullable String getUrl() {
        return this.uri;
    }

    public @Nullable String getMusicUri() {
        return this.musicUri;
    }

    @Override
    public @Nullable String getThumbnailUrl() {
        return this.thumbnailUri;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<String> getVideoIds() {
        return this.videoIds;
    }
}
