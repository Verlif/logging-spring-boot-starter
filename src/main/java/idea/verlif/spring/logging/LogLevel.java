package idea.verlif.spring.logging;

/**
 * 日志等级
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/11/26 14:42
 */
public enum LogLevel {

    /**
     * debug
     */
    DEBUG(0),
    /**
     * info
     */
    INFO(1),
    /**
     * warning
     */
    WARNING(2),
    /**
     * error
     */
    ERROR(3);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
