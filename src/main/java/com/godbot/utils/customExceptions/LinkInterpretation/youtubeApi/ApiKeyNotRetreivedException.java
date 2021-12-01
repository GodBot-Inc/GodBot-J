package utils.customExceptions.LinkInterpretation.youtubeApi;

public class ApiKeyNotRetreivedException extends RuntimeException {
    public ApiKeyNotRetreivedException(String errorMessage) {
        super(errorMessage);
    }
}
