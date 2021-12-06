package com.godbot.utils.interpretations.youtube;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class YoutubePlaylistInterpretation implements YoutubeInterpretation {

    public static class PlaylistBuilder {

        private long duration = 0;
        private String creator;
        private String creatorUri;
        private String title;
        private String uri;
        private String thumbnailUri;
        private int size;
        private ArrayList<String> videoIds;

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
    public String getCreatorLink() {
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
