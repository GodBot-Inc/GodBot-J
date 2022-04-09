package logging;

import utils.MongoCommunication;

import java.util.logging.Logger;

public class LinkProcessingLogger implements DefaultLogger {

    private static final LinkProcessingLogger loggerObj = new LinkProcessingLogger();
    private final Logger logger;
    private final MongoCommunication dbCommunication;

    private LinkProcessingLogger() {
        this.logger = Logger.getLogger("LinkProcessingLogger");
        this.dbCommunication = MongoCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    public void infoAndSave(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    public static LinkProcessingLogger getInstance() {
        return loggerObj;
    }
}
