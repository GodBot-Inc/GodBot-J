package utils.logging;

import java.util.logging.Logger;

import utils.database.MongoCommunication;

public class AudioLogger implements DefaultLogger {

    private final Logger logger;
    private final MongoCommunication dbCommunication;

    public AudioLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        dbCommunication = MongoCommunication.getInstance();
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
