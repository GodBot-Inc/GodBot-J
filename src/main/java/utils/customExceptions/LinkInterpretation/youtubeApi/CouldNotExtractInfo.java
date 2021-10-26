package utils.customExceptions.LinkInterpretation.youtubeApi;

public class CouldNotExtractInfo extends RuntimeException {
    public CouldNotExtractInfo(String errorMessage) {
        super(errorMessage);
    }
}
