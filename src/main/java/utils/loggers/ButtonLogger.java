package utils.loggers;

import java.util.logging.Logger;

public class ButtonLogger {

    private static final ButtonLogger loggerObj = new ButtonLogger();
    private final Logger logger;

    private ButtonLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public static ButtonLogger getInstance() { return loggerObj; }

    public void info(String infoMessage) {
        logger.info(infoMessage);
    }

    public void warn(String warnMessage) {
        logger.warning(warnMessage);
    }

}
