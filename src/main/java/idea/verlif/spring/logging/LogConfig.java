package idea.verlif.spring.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/27 16:16
 */
@Configuration
@ConfigurationProperties(prefix = "station.log")
public class LogConfig {

    private static final String SPLIT = ",";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogConfig.class);

    /**
     * 日志开启等级，使用英文,隔开
     */
    private String level;

    /**
     * 日志启用类型（全类名），使用英文,隔开
     */
    private String type;

    private final List<LogLevel> allowedLevel;
    private final List<String> allowedType;

    public LogConfig() {
        allowedLevel = new ArrayList<>();
        allowedType = new ArrayList<>();
    }

    public void setLevel(String level) {
        this.level = level;
        for (String s : level.split(SPLIT)) {
            String t = s.trim();
            if (t.length() == 0) {
                continue;
            }
            try {
                LogLevel l = LogLevel.valueOf(t.toUpperCase(Locale.ROOT));
                allowedLevel.add(l);
            } catch (Exception ignored) {
            }
        }
        if (allowedType.size() > 0) {
            LOGGER.info("Log allowed level - " + allowedType);
        }
    }

    public String getLevel() {
        return level;
    }

    /**
     * 是否允许此等级的日志进行记录
     *
     * @param level 目标等级
     * @return 是否允许
     */
    public boolean levelable(LogLevel level) {
        return this.level == null || allowedLevel.contains(level);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        for (String s : type.split(SPLIT)) {
            String t = s.trim();
            if (t.length() == 0) {
                continue;
            }
            try {
                Class<?> cl = Class.forName(t);
                allowedType.add(cl.getSimpleName());
            } catch (Exception ignored) {
            }
        }
        if (allowedType.size() > 0) {
            LOGGER.info("Log allowed type - " + allowedType);
        }
    }

    /**
     * 是否允许此类型的日志进行记录
     *
     * @param type 目标类型
     * @return 是否允许
     */
    public boolean typeable(Class<?> type) {
        return this.type == null || allowedType.contains(type.getSimpleName());
    }

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
