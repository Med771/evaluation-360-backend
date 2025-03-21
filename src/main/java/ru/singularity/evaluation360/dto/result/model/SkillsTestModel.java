package ru.singularity.evaluation360.dto.result.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для оценки навыков в тесте.
 */
@Schema(description = "Оценка навыков в тесте")
public record SkillsTestModel(

        @Schema(description = "ID навыка", example = "12")
        Integer skillId,

        @Schema(description = "Среднее значение оценки навыка", example = "3.8")
        Double value
) {}
