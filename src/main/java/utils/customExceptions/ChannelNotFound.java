package utils.customExceptions;

public class ChannelNotFound extends Exception {
    public ChannelNotFound(String errorMessage) {
        super(errorMessage);
    }
}