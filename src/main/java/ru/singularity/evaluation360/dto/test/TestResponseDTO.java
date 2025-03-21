package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;

import java.util.List;

/**
 * DTO для получения информации о тесте.
 */
@Schema(description = "DTO для получения информации о тесте")
public record TestResponseDTO(
        @Schema(description = "Название теста", example = "Оценка навыков")
        String title,

        @Schema(description = "ID оцениваемого", example = "2001")
        Long evaluatedId,

        @Schema(description = "ID оценщика", example = "3001")
        Long evaluatorId,

        @Schema(description = "Список вопросов теста")
        List<QuestionTestModel> questions) {
}
