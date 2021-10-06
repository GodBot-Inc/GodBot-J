package utils.CustomExceptions;

public class GuildNotFound extends Exception {
    public GuildNotFound(String errorMessage) {
        super(errorMessage);
    }
}
