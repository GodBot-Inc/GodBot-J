package utils.CustomExceptions.audio;

public class ApplicationNotFound extends Exception {
    public ApplicationNotFound(String errorMessage) {
        super(errorMessage);
    }
}
