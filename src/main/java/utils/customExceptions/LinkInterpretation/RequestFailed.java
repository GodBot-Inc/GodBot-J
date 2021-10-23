package utils.customExceptions.LinkInterpretation;

public class RequestFailed extends Exception {
    public RequestFailed(String errorMessage) {
        super(errorMessage);
    }
}
