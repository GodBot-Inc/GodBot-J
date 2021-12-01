package utils.customExceptions.checks;

public class VoiceCheckFailedException extends RuntimeException {
    public VoiceCheckFailedException(String errorMessage) {
        super(errorMessage);
    }
}
