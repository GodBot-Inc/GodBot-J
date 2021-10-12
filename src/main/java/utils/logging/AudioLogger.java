package utils.logging;

import java.util.logging.Logger;

import utils.DBCommunication;
import utils.LoggerContent;

public class AudioLogger implements DefaultLogger {

    private final Logger logger;
    private final DBCommunication dbCommunication;

    public AudioLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        dbCommunication = DBCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj);
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.warning(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj);
    }
}
