package com.godbot.utils.customExceptions.audio;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
