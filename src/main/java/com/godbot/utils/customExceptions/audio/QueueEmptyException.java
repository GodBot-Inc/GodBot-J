package com.godbot.utils.customExceptions.audio;


public class QueueEmptyException extends Exception {
    public QueueEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
