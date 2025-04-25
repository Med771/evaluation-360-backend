package ru.singularity.evaluation360.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.singularity.evaluation360.log.annotation.LogMethod;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class MethodLoggingAspect {

    @Around("@annotation(logMethod)")
    public Object logMethod(ProceedingJoinPoint joinPoint, LogMethod logMethod) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Логирование входа
        if (logMethod.logArgs()) {
            LogUtil.logAtLevel(logMethod.level(),"Method {} in class {} called with args: {}",
                    methodName, className, Arrays.toString(joinPoint.getArgs()));
        }

        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // Логирование результата
            if (logMethod.logResult()) {
                LogUtil.logAtLevel(logMethod.level(),"Method {} in class {} completed with result: {}",
                        methodName, className, result);
            }

            // Логирование времени выполнения
            if (logMethod.logExecutionTime()) {
                LogUtil.logAtLevel(logMethod.level(),"Method {} in class {} executed in {} ms",
                        methodName, className, executionTime);
            }
        }
    }
}
