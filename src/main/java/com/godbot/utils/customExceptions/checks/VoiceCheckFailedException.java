package com.godbot.utils.customExceptions.checks;

public class VoiceCheckFailedException extends CheckFailedException {
    public VoiceCheckFailedException(String errorMessage) {
        super(errorMessage);
    }
}
