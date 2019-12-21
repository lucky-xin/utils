package com.xin.utils.log;

import org.apache.log4j.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: Log 工厂类
 * @date 7:26 2018-06-25
 **/
public class LogFactory {

    private static Map<String, Logger> loggers = new ConcurrentHashMap<>();

    public static Logger getLogger(String loggerFileName) {
        Logger logger = loggers.get(loggerFileName);
        if (logger != null) {
            return logger;
        }
        return generateLogger(loggerFileName, Level.DEBUG);
    }

    public static Logger getLogger(String loggerFileName, Level level) {
        Logger logger = loggers.get(loggerFileName);
        if (logger != null) {
            return logger;
        }
        return generateLogger(loggerFileName, level);
    }

    private static Logger generateLogger(String loggerFileName, Level level) {
        Logger logger = Logger.getLogger(loggerFileName);
        if (logger == null) {
            throw new RuntimeException("获取Logger 错误");
        }
        logger.removeAllAppenders();
        //设置继承输出root
        logger.setAdditivity(false);
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss,SSS} [%p] [%M line:%L]%n %m%n");
        try {
            Appender appender = new DailyRollingFileAppender(layout, "../logs/" + loggerFileName + ".com.xin.utils.log", "yyyy-MM-dd");
            logger.addAppender(appender);
            logger.setLevel(level);
        } catch (IOException e) {
            throw new RuntimeException("创建日志文件失败", e);
        }
        if (logger == null) {
            throw new RuntimeException("获取Logger失败");
        }
        loggers.put(loggerFileName, logger);
        return logger;
    }
}
