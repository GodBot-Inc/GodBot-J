package utils.customExceptions;

public class ChannelNotFoundException extends Exception {
    public ChannelNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
