package ru.singularity.evaluation360.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.singularity.evaluation360.log.annotation.LogException;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "@annotation(logException)", throwing = "ex")
    public void logException(JoinPoint joinPoint, LogException logException, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        LogUtil.logAtLevel(logException.level(),"Exception in method {} in class {}: {}",
                methodName, className, ex.getMessage());

        if (logException.logStackTrace()) {
            LogUtil.logAtLevel(logException.level(),"Stack trace:", ex);
        }
    }
}
