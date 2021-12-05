package com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
