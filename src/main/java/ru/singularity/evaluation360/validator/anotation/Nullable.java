package ru.singularity.evaluation360.validator.anotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {
}
