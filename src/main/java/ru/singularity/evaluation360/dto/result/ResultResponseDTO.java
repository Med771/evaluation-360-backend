package ru.singularity.evaluation360.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

import java.util.List;

/**
 * DTO с результатами теста.
 */
@ValidateFieldsNotNullOrBlank
@Schema(description = "Результаты теста")
public record ResultResponseDTO(

        @Schema(description = "Название теста", example = "Оценка командной работы")
        String title,

        @Schema(description = "Средний результат", example = "4.2")
        Double averageResult,

        @Schema(description = "Личный результат", example = "4.0")
        Double thisResult,

        @Schema(description = "Результат команды", example = "4.3")
        Double commandResult,

        @Schema(description = "Результат экспертов", example = "4.1")
        Double expertResult,

        @Schema(description = "Результаты по навыкам")
        List<SkillsResultModel> results,

        @Schema(description = "Финальный комментарий", example = "В целом хороший результат, но есть куда расти")
        String resultComment
) {}
