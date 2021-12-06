package com.godbot.utils.customExceptions.audio;


public class QueueEmptyException extends RuntimeException {
    public QueueEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
