package ru.singularity.evaluation360.dto.test.model;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

/**
 * Модель заголовка респондента в тесте.
 */
@ValidateFieldsNotNullOrBlank
@Schema(description = "Модель заголовка респондента в тесте")
public record TestRespondentTitleModel(
        @Schema(description = "Идентификатор респондента", example = "101")
        int respondentId,

        @Schema(description = "Полное имя респондента", example = "Иван Иванов")
        String fullName,

        @Schema(description = "Флаг завершенности теста респондентом", example = "true")
        Boolean isComplete) {
}
