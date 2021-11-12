package utils.customExceptions;

public class JDANotFoundException extends RuntimeException {
    public JDANotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
