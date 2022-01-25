package idea.verlif.spring.logging.api.impl;

import idea.verlif.spring.logging.api.LogIt;
import idea.verlif.spring.logging.LogService;
import idea.verlif.spring.logging.api.ApiLogHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/1/10 14:21
 */
@Component
public class DefaultApiLogHandler implements ApiLogHandler {

    @Autowired
    private LogService logService;

    @Override
    public void onLog(Method method, LogIt logIt) {
        logService.log(logIt.level(), method.getName() + " >> " + logIt.message());
    }

    @Override
    public void onReturn(Method method, LogIt logIt, Object o) {
        logService.log(logIt.level(), method.getName() + " return >> " + o);
    }

}