package idea.verlif.spring.logging;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/27 16:12
 */
public interface LogService {

    /**
     * DEBUG
     *
     * @param msg 信息
     */
    void debug(String msg);

    /**
     * INFO
     *
     * @param msg 信息
     */
    void info(String msg);

    /**
     * WARN
     *
     * @param msg 信息
     */
    void warn(String msg);

    /**
     * ERROR
     *
     * @param msg 信息
     */
    void error(String msg);

    /**
     * 统一的log方法
     *
     * @param level 日志等级
     * @param msg   信息
     */
    void log(LogLevel level, String msg);
}
