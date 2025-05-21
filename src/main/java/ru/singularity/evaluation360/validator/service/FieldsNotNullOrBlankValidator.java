package ru.singularity.evaluation360.validator.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.singularity.evaluation360.validator.anotation.Nullable;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

import java.lang.reflect.Field;

public class FieldsNotNullOrBlankValidator implements ConstraintValidator<ValidateFieldsNotNullOrBlank, Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) return true;

        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Nullable.class)) {
                continue;
            }

            try {
                Object value = field.get(object);

                if (value == null) {
                    context.buildConstraintViolationWithTemplate(
                            field.getName() + " не должно быть null"
                    ).addPropertyNode(field.getName()).addConstraintViolation();
                    return false;
                }

                if (value instanceof String && ((String) value).isBlank()) {
                    context.buildConstraintViolationWithTemplate(
                            field.getName() + " не должно быть пустой строкой"
                    ).addPropertyNode(field.getName()).addConstraintViolation();
                    return false;
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
