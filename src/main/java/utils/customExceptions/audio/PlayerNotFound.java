package utils.customExceptions.audio;

public class PlayerNotFound extends Exception {
    public PlayerNotFound(String errorMessage) {
        super(errorMessage);
    }
}
