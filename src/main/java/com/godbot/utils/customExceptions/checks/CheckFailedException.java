package utils.customExceptions.checks;

public class CheckFailedException extends RuntimeException {
    public CheckFailedException(String errorMessage) {
        super(errorMessage);
    }
}
