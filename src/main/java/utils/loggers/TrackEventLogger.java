package utils.loggers;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TrackEventLogger {

    public static final TrackEventLogger loggerObj = new TrackEventLogger();

    private final Logger logger;

    private TrackEventLogger() {
        Dotenv dotenv = Dotenv.load();
        String LOGGER_DIR = dotenv.get("LOGGER_DIR");

        logger = Logger.getLogger("TrackEventLogger");
        FileHandler fh;
        try {
            fh = new FileHandler(LOGGER_DIR + "\\TrackEvents.log");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    public static TrackEventLogger getLogger() { return loggerObj; }

    public void log(String logMessage) {
        logger.info(logMessage);
    }

}
