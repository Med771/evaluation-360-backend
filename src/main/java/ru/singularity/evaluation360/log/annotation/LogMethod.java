package ru.singularity.evaluation360.log.annotation;

import org.slf4j.event.Level;
import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethod {
    String value() default "";
    Level level() default Level.INFO;
    boolean logArgs() default true;
    boolean logResult() default true;
    boolean logExecutionTime() default true;
}
