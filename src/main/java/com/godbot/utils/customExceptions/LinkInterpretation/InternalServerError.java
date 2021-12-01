package utils.customExceptions.LinkInterpretation;

public class InternalServerError extends RuntimeException {
    public InternalServerError(String errorMessage) {
        super(errorMessage);
    }
}
