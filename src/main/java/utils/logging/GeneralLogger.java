package utils.logging;

import java.util.logging.Logger;

import utils.DBCommunication;

public class GeneralLogger implements DefaultLogger {
    
    private final Logger logger;
    private final DBCommunication dbCommunication;

    public GeneralLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        this.dbCommunication = DBCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        this.dbCommunication.generalLog(loggerObj);
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.warning(loggerObj.getAsString());
        this.dbCommunication.generalLog(loggerObj);
    }
}
