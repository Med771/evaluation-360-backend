package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;

import java.util.List;

@Schema(description = "просмотр теста от администратора")
public record TestViewResponseDTO(
        @Schema(description = "Название теста")
        String title,

        @Schema(description = "Тип теста")
        TypeTestEnum type,

        @Schema(description = "Статус теста")
        StatusTestEnum status,

        @Schema(description = "Время создания теста")
        Long createTimeStamp,

        @Schema(description = "Время начала теста")
        Long startTimeStamp,

        @Schema(description = "Время конца теста")
        Long endTimeStamp,

        @Schema(description = "Респонденты")
        List<RespondentModel> respondents,

        @Schema(description = "Вопросы теста")
        List<QuestionModel> questions,

        @Schema(description = "минимум респондентов")
        Integer minRespondents,

        @Schema(description = "максимум респондентов")
        Integer maxRespondents,

        @Schema(description = "минимум высоких ролей")
        Integer minHighRoleRespondents,

        @Schema(description = "минимум других курсов")
        Integer minOtherCourseRespondents) {
}
