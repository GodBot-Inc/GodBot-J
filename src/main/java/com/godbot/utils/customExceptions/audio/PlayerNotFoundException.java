package com.godbot.utils.customExceptions.audio;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
