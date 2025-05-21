package ru.singularity.evaluation360.dto.test.model;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

/**
 * Модель заголовка теста.
 */
@ValidateFieldsNotNullOrBlank
@Schema(description = "Модель заголовка теста")
public record TestTitleModel(
        @Schema(description = "Идентификатор теста", example = "10")
        String testId,

        @Schema(description = "Название теста", example = "Оценка сотрудников")
        String title,

        @Schema(description = "Временная метка начала теста (в миллисекундах)", example = "1672531200000")
        Long startTimeStamp,

        @Schema(description = "Временная метка окончания теста (в миллисекундах)", example = "1672617600000")
        Long endTimeStamp) {
}
