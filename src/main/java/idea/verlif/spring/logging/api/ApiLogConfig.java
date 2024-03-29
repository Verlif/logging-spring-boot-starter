package idea.verlif.spring.logging.api;

import idea.verlif.spring.logging.LogLevel;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/27 16:16
 */
@Configuration
@ConfigurationProperties(prefix = "station.api-log")
public class ApiLogConfig {

    private static final String SPLIT = ",";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ApiLogConfig.class);

    /**
     * 日志开启等级，使用英文,隔开
     */
    private String level;

    /**
     * 日志启用类型（短类名），使用英文,隔开
     */
    private String type;

    /**
     * 允许的记录等级
     */
    private final List<LogLevel> allowedLevel;

    /**
     * 允许的记录类型
     */
    private final List<String> allowedType;

    private final ThreadPoolInfo poolInfo;

    public ApiLogConfig() {
        this.allowedLevel = new ArrayList<>();
        this.allowedType = new ArrayList<>();

        this.poolInfo = new ThreadPoolInfo();
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
        if (allowedLevel.size() > 0) {
            LOGGER.info("Api log allowed level - " + allowedLevel);
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
        return this.level == null || allowedLevel.size() == 0 || allowedLevel.contains(level);
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
            allowedType.add(t);
        }
        if (allowedType.size() > 0) {
            LOGGER.info("Api log allowed type - " + allowedType);
        }
    }

    /**
     * 是否允许此类型的日志进行记录
     *
     * @param type 目标类型
     * @return 是否允许
     */
    public boolean typeable(Class<?> type) {
        return this.type == null || allowedType.size() == 0 || allowedType.contains(type.getSimpleName());
    }

    public ThreadPoolInfo getPoolInfo() {
        return poolInfo;
    }

    /**
     * 线程池信息
     */
    public static final class ThreadPoolInfo {

        /**
         * 线程池最大值
         */
        private int max = 200;

        /**
         * 日志等待队列长度
         */
        private int length = 1000;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }
}
