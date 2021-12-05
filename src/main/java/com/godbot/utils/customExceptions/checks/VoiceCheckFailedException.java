package com.godbot.utils.customExceptions.checks;

public class VoiceCheckFailedException extends RuntimeException {
    public VoiceCheckFailedException(String errorMessage) {
        super(errorMessage);
    }
}
