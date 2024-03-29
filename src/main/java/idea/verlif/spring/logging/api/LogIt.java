package idea.verlif.spring.logging.api;

import idea.verlif.spring.logging.LogLevel;
import idea.verlif.spring.logging.api.impl.DefaultApiLogHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/11/26 14:20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIt {

    /**
     * 日志类型，使用不同的类作为区分。推荐使用接口类。
     */
    Class<?> type() default Object.class;

    /**
     * 记录附加信息
     */
    String message();

    /**
     * 日志等级
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * 是否同步进行日志处理。默认同步
     */
    boolean sync() default true;

    /**
     * 是否控制调用顺序
     */
    boolean order() default false;

    /**
     * 日志处理类
     */
    Class<? extends ApiLogHandler> handler() default DefaultApiLogHandler.class;

}
