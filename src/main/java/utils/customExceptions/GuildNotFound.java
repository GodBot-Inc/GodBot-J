package utils.customExceptions;

public class GuildNotFound extends Exception {
    public GuildNotFound(String errorMessage) {
        super(errorMessage);
    }
}
