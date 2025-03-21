package ru.singularity.evaluation360.dto.result.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * DTO с результатами по навыкам.
 */
@Schema(description = "Результаты по навыкам")
public record SkillsResultModel(

        @Schema(description = "Название навыка", example = "Командная работа")
        String skillName,

        @Schema(description = "Средняя оценка по навыку", example = "4.2")
        Double averageEvaluation,

        @Schema(description = "Личная оценка", example = "4.0")
        Double thisEvaluation,

        @Schema(description = "Оценка команды", example = "4.3")
        Double commandEvaluation,

        @Schema(description = "Оценка эксперта", example = "4.1")
        Double expertEvaluation,

        @Schema(description = "Комментарии", example = "[\"Очень хорошо работает в команде\", \"Немного медлит в принятии решений\"]")
        List<String> comments
) {}
