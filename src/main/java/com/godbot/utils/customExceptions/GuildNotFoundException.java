package utils.customExceptions;

public class GuildNotFoundException extends Exception {
    public GuildNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
