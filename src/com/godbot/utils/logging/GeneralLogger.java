package utils.logging;

import java.util.logging.Logger;

import utils.database.MongoCommunication;

public class GeneralLogger implements DefaultLogger {
    
    private final Logger logger;

    public GeneralLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.warning(loggerObj.getAsString());
    }
}
