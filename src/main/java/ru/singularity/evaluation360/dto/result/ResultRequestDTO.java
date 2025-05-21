package ru.singularity.evaluation360.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

import java.util.List;

/**
 * DTO для отправки результатов теста.
 */
@ValidateFieldsNotNullOrBlank
@Schema(description = "Отправка результатов после теста")
public record ResultRequestDTO(

        @Schema(description = "ID оцениваемого пользователя", example = "456")
        Integer evaluatedId,

        @Schema(description = "ID оценивающего пользователя", example = "789")
        Integer evaluatorId,

        @Schema(description = "Время начала теста (Unix timestamp)", example = "1710567890")
        Long startTimeStamp,

        @Schema(description = "Время окончания теста (Unix timestamp)", example = "1710571490")
        Long endTimeStamp,

        @Schema(description = "Ответы на тестовые вопросы")
        List<AnswerTestModel> answers,

        @Schema(description = "Оценки навыков")
        List<SkillsTestModel> skills
) {}
