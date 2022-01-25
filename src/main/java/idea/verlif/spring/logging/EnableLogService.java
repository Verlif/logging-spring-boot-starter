package idea.verlif.spring.logging;

import idea.verlif.spring.logging.api.ApiLogManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启日志服务
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/1/25 10:17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Configuration
@Documented
@Import({LogConfig.class, ApiLogManager.class})
public @interface EnableLogService {
}
