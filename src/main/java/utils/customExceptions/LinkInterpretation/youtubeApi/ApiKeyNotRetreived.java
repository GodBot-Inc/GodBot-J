package utils.customExceptions.LinkInterpretation.youtubeApi;

public class ApiKeyNotRetreived extends RuntimeException {
    public ApiKeyNotRetreived(String errorMessage) {
        super(errorMessage);
    }
}
