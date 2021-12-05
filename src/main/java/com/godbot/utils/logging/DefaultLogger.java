package com.godbot.utils.logging;

import java.util.logging.Logger;

public interface DefaultLogger {

     Logger logger = Logger.getLogger("DefaultLogger");


     default void info(LoggerContent loggerObj) {
         logger.info(loggerObj.getAsString());
         // Save into specific Database
     }

     default void warn(LoggerContent loggerObj) {
         logger.warning(loggerObj.getAsString());
         // Save into specific Database
     }
}
