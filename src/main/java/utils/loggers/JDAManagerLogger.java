package utils.loggers;

import java.util.logging.Logger;

public class JDAManagerLogger {

    private static final JDAManagerLogger loggerObj = new JDAManagerLogger();
    private final Logger logger;

    private JDAManagerLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public static JDAManagerLogger getInstance() { return loggerObj; }

    public void info(String infoMessage) {
        logger.info(infoMessage);
    }

    public void warn(String warnMessage) {
        logger.warning(warnMessage);
    }
}
