package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;

import java.util.List;
import java.util.Map;

@Schema(description = "создание теста")
public record TestRequestDTO(
        @Schema(description = "название теста")
        String title,

        @Schema(description = "тип теста")
        TypeTestEnum type,

        @Schema(description = "время создания теста")
        Long createTimeStamp,

        @Schema(description = "время начало теста")
        Long startTimeStamp,

        @Schema(description = "Время окончания теста")
        Long endTimeStamp,

        @Schema(description = "выбранный вопрос")
        Map<Integer, String> questionIds,

        @Schema(description = "новый вопрос")
        Map<Integer, QuestionTestModel> newQuestions,

        @Schema(description = "id участников")
        List<Integer> participantsIds,

        @Schema(description = "минимум респондентов")
        Integer minRespondents,

        @Schema(description = "максимум респондентов")
        Integer maxRespondents,

        @Schema(description = "минимум высоких ролей")
        Integer minHighRoleRespondents,

        @Schema(description = "минимум других курсов")
        Integer minOtherCourseRespondents,

        @Schema(description = "новые скиллы")
        List<SkillRequestDTO> newSkills
        )

{ }
