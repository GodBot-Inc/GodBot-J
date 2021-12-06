package com.godbot.utils.customExceptions.ytApi;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
