package utils.logging;

import java.util.logging.Logger;

import utils.database.MongoCommunication;

public class GeneralLogger implements DefaultLogger {
    
    private final Logger logger;
    private final MongoCommunication dbCommunication;

    public GeneralLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        this.dbCommunication = MongoCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        this.dbCommunication.generalLog(loggerObj.getDBScheme());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.warning(loggerObj.getAsString());
        this.dbCommunication.generalLog(loggerObj.getDBScheme());
    }
}
