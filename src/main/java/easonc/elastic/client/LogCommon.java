package easonc.elastic.client;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by zhuqi on 2016/11/16.
 */
public final class LogCommon {

    private static Logger logger = LogManager.getLogger(LogCommon.class);

    public static void logError(Throwable e) {
        logError(null, e);
    }

    public static void logError(String title, Throwable e) {
        if (Strings.isWhitespaceOrNull(title)) {
            title = "NA";
        }
        logger.error(title, e);
    }

    public static void logError(String title, String message) {
        logger.error(message);
    }

    public static void logError(Logger logger, String title, Throwable e, Map<String, String> attr) {
        logger.error(title, e);
    }


    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logInfo(String title, String message) {
        logger.info(message);
    }

    public static void logInfo(String title, String message, Map<String, String> attr) {
        logger.info(message);
    }

    public static void logInfo(String title, Throwable e) {
        logger.info(title, e);
    }

    public static void logInfo(String title, Throwable e, Map<String, String> attr) {
        logger.info(title, e);
    }

    public static void logInfo(Logger logger, String title, Throwable e, Map<String, String> attr) {
        logger.info(title, e);
    }
}
