package com.godbot.utils.customExceptions.audio;

public class ApplicationNotFoundException extends Exception {
    public ApplicationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
