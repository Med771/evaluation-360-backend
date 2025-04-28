package ru.singularity.evaluation360.log.annotation;

import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogEntryExit {
    String value() default "";
    Level level() default Level.DEBUG;
    boolean logArgs() default true;
    boolean logResult() default true;
}
