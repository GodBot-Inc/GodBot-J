package utils.loggers;

import java.util.logging.Logger;

public class AudioManagerManagerLogger {

    private static final AudioManagerManagerLogger loggerObj = new AudioManagerManagerLogger();
    private final Logger logger;

    private AudioManagerManagerLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public static AudioManagerManagerLogger getInstance() { return loggerObj; }

    public void info(String infoMessage) {
        logger.info(infoMessage);
    }

    public void warn(String warnMessage) {
        logger.warning(warnMessage);
    }

}
