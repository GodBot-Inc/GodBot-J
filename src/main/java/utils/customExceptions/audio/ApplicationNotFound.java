package utils.customExceptions.audio;

public class ApplicationNotFound extends Exception {
    public ApplicationNotFound(String errorMessage) {
        super(errorMessage);
    }
}
