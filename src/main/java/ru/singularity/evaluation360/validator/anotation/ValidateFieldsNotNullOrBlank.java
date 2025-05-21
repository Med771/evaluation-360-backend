package ru.singularity.evaluation360.validator.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.singularity.evaluation360.validator.service.FieldsNotNullOrBlankValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsNotNullOrBlankValidator.class)
public @interface ValidateFieldsNotNullOrBlank {
    String message() default "Некорректные значения в полях объекта";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
