package ru.singularity.evaluation360.dto.result.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

/**
 * DTO для хранения ответа на тестовый вопрос.
 */
@Schema(description = "Ответ на тестовый вопрос")
@ValidateFieldsNotNullOrBlank
public record AnswerTestModel(

        @Schema(description = "Значение ответа (0-4)", example = "3")
        @Min(0)
        @Max(4)
        Integer value,

        @Schema(description = "Комментарий к ответу", example = "Отличный вопрос, но не совсем понял формулировку")
        String comment
) {}
