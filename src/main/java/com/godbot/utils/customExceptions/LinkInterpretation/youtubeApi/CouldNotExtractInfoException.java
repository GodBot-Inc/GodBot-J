package com.godbot.utils.customExceptions.LinkInterpretation.youtubeApi;

public class CouldNotExtractInfoException extends RuntimeException {
    public CouldNotExtractInfoException(String errorMessage) {
        super(errorMessage);
    }
}
