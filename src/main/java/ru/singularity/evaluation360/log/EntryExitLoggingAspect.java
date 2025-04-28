package ru.singularity.evaluation360.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.singularity.evaluation360.log.annotation.LogEntryExit;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class EntryExitLoggingAspect {

    @Around("@annotation(logEntryExit)")
    public Object logEntryExit(ProceedingJoinPoint joinPoint, LogEntryExit logEntryExit) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Логирование входа
        if (logEntryExit.logArgs()) {
            LogUtil.logAtLevel(logEntryExit.level(),"Entering method {} in class {} with args: {}",
                    methodName, className, Arrays.toString(joinPoint.getArgs()));
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            // Логирование выхода
            if (logEntryExit.logResult()) {
                LogUtil.logAtLevel(logEntryExit.level(),"Exiting method {} in class {} with result: {}",
                        methodName, className, result);
            }
        }
    }
}
