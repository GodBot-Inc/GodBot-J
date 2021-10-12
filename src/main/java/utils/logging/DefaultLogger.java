package utils.logging;

import utils.LoggerObj;

import java.util.logging.Logger;

public interface DefaultLogger {

     Logger logger = Logger.getLogger("DefaultLogger");


     default void info(LoggerObj loggerObj) {
         logger.info(loggerObj.getAsString());
         // Save into specific Database
     }

     default void warn(LoggerObj loggerObj) {
         logger.warning(loggerObj.getAsString());
         // Save into specific Database
     }
}
