package utils.customExceptions;

public class InvalidURL extends Exception {
    public InvalidURL(String errorMessage) {
        super(errorMessage);
    }
}
