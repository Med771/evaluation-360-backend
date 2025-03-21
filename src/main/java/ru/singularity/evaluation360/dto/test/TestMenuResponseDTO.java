package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;

import java.util.List;

/**
 * DTO для меню тестов.
 */
@Schema(description = "DTO для меню тестов")
public record TestMenuResponseDTO(
        @Schema(description = "Название теста", example = "Оценка 360")
        String title,

        @Schema(description = "Флаг получения списка респондентов", example = "true")
        Boolean isGetRespondents,

        @Schema(description = "Флаг выбора респондентов", example = "false")
        Boolean isSelectRespondents,

        @Schema(description = "Флаг завершенности процесса оценки", example = "true")
        Boolean isCompleteEvaluation,

        @Schema(description = "Флаг завершенности тестирования оцениваемых", example = "true")
        Boolean isCompleteEvaluated,

        @Schema(description = "Флаг завершенности тестирования оценщиков", example = "false")
        Boolean isCompeteEvaluator,

        @Schema(description = "Список оцениваемых респондентов")
        List<TestRespondentTitleModel> evaluatedRespondents,

        @Schema(description = "Список оценщиков")
        List<TestRespondentTitleModel> evaluatorRespondents,

        @Schema(description = "Флаг активности результата теста", example = "true")
        Boolean isActiveResult) {
}
