package utils.logging;

import utils.database.MongoCommunication;

import java.util.logging.Logger;

public class ListenerLogger implements DefaultLogger{

    private final Logger logger;
    private final MongoCommunication dbCommunication;

    public ListenerLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        dbCommunication = MongoCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    public void infoAndSave(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        dbCommunication.commandLog(loggerObj.getDBScheme());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj.getDBScheme());
    }
}