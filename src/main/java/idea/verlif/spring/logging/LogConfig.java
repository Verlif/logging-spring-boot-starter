package idea.verlif.spring.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/1/25 11:53
 */
@Configuration
public class LogConfig {

    @Bean
    @ConditionalOnMissingBean(LogService.class)
    public LogService logService() {
        return new DefaultLogService();
    }

    private static class DefaultLogService implements LogService {

        public final Map<String, Logger> loggerMap;

        public DefaultLogService() {
            loggerMap = new HashMap<>();
        }

        @Override
        public void debug(String msg) {
            getLogger().debug(msg);
        }

        @Override
        public void info(String msg) {
            getLogger().info(msg);
        }

        @Override
        public void warn(String msg) {
            getLogger().warn(msg);
        }

        @Override
        public void error(String msg) {
            getLogger().error(msg);
        }

        @Override
        public void log(LogLevel level, String msg) {
            switch (level) {
                case INFO:
                    info(msg);
                    break;
                case WARNING:
                    warn(msg);
                    break;
                case ERROR:
                    error(msg);
                    break;
                case DEBUG:
                default:
                    debug(msg);
                    break;
            }
        }

        private Logger getLogger() {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            String cl = elements[3].getClassName();
            Logger logger = loggerMap.get(cl);
            if (logger == null) {
                logger = LoggerFactory.getLogger(cl);
                loggerMap.put(cl, logger);
            }
            return logger;
        }
    }
}
