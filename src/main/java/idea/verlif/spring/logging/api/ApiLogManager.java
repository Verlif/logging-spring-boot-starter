package idea.verlif.spring.logging.api;

import idea.verlif.spring.logging.LogService;
import idea.verlif.spring.logging.api.impl.DefaultApiLogHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/26 14:26
 */
@Aspect
@Component
@ConditionalOnProperty(prefix = "station.api-log", value = "enabled", matchIfMissing = true)
@Import({ApiLogConfig.class, DefaultApiLogHandler.class})
public final class ApiLogManager {

    private final LogService logService;
    private final ApiLogConfig apiLogConfig;
    private final ThreadPoolExecutor executor;
    private final Map<Class<? extends ApiLogHandler>, ApiLogHandler> handlerMap;

    @Autowired
    public ApiLogManager(
            ApplicationContext context,
            LogService logService,
            ApiLogConfig config) {
        ApiLogConfig.ThreadPoolInfo poolInfo = config.getPoolInfo();
        this.logService = logService;
        this.apiLogConfig = config;
        this.executor = new ThreadPoolExecutor(
                poolInfo.getMax() / 2 + 1, poolInfo.getMax(),
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(poolInfo.getLength()),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("ApiLogThread-" + thread.getId());
                    return thread;
                });
        this.handlerMap = new HashMap<>();
        Map<String, ApiLogHandler> map = context.getBeansOfType(ApiLogHandler.class);
        for (ApiLogHandler handler : map.values()) {
            this.handlerMap.put(handler.getClass(), handler);
        }
    }

    @Around("@within(idea.verlif.spring.logging.api.LogIt) || @annotation(idea.verlif.spring.logging.api.LogIt)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        Method method = ((MethodSignature) sig).getMethod();

        // 检测方法上的注解
        LogIt logIt = method.getAnnotation(LogIt.class);
        if (logIt == null) {
            logIt = method.getDeclaringClass().getAnnotation(LogIt.class);
        }
        // 记录过滤
        if (apiLogConfig.levelable(logIt.level())) {
            if (apiLogConfig.typeable(logIt.type())) {
                ApiLogHandler handler = handlerMap.get(logIt.handler());
                if (handler != null) {
                    LogIt finalLogIt = logIt;
                    if (logIt.sync()) {
                        handler.onLog(method, finalLogIt, joinPoint.getArgs(), System.currentTimeMillis());
                    } else if (!logIt.order()) {
                        executor.execute(() -> handler.onLog(method, finalLogIt, joinPoint.getArgs(), System.currentTimeMillis()));
                    }
                    Object o = joinPoint.proceed();
                    if (logIt.sync()) {
                        handler.onReturn(method, finalLogIt, o, System.currentTimeMillis());
                    } else if (logIt.order()) {
                        executor.execute(() -> {
                            handler.onLog(method, finalLogIt, joinPoint.getArgs(), System.currentTimeMillis());
                            handler.onReturn(method, finalLogIt, o, System.currentTimeMillis());
                        });
                    } else {
                        executor.execute(() -> handler.onReturn(method, finalLogIt, o, System.currentTimeMillis()));
                    }
                    return o;
                } else {
                    logService.warn(method.getName() + " has not be logged - " + logIt.handler().getSimpleName());
                }
            }
        }
        return joinPoint.proceed();
    }
}
