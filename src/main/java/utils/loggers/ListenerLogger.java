package utils.loggers;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ListenerLogger {

    public static final ListenerLogger loggerObj = new ListenerLogger();

    private final Logger logger;
    private long linesLogged;

    private ListenerLogger() {
        linesLogged = 0;

        Dotenv dotenv = Dotenv.load();
        String LOGGER_DIR = dotenv.get("LOGGER_DIR");

        logger = Logger.getLogger("ListenerLogger");
        FileHandler fh;
        try {
            fh = new FileHandler(LOGGER_DIR + "\\Listener.log");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    public static ListenerLogger getLogger() {
        return loggerObj;
    }

    public void log (String logMessage) {
        logger.info(logMessage);
        linesLogged++;
        checkLines();
    }

    public void warn (String warnMessage) {
        logger.warning(warnMessage);
        linesLogged++;
        checkLines();
    }

    public void checkLines() {
        if (linesLogged >= 5000) {
            // TODO fix potential errors
            System.out.println("resources/logs/Listener.log is full");
        }
    }
}
