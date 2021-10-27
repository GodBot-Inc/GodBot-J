package utils.customExceptions.LinkInterpretation.youtubeApi;

public class VideoNotFound extends RuntimeException {
    public VideoNotFound(String errorMessage) {
        super(errorMessage);
    }
}
