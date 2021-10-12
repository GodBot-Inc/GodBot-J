package utils.customExceptions.audio;


public class QueueEmpty extends Exception {
    public QueueEmpty(String errorMessage) {
        super(errorMessage);
    }
}
