package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

import java.util.List;

/**
 * DTO для списка тестов.
 */
@ValidateFieldsNotNullOrBlank
@Schema(description = "DTO для списка тестов")
public record TestsResponseDTO(
        @Schema(description = "Название группы тестов", example = "Годовая аттестация")
        String nameGroup,

        @Schema(description = "Список тестов")
        List<TestTitleModel> tests) {
}
