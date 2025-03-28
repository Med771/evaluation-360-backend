package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;

import java.util.List;

public record TestViewResponseDTO(
        String title,

        TypeTestEnum type,
        StatusTestEnum status,

        Long createTimeStamp,
        Long startTimeStamp,
        Long endTimeStamp,

        List<RespondentModel> respondents,
        List<QuestionModel> questions,

        Integer minRespondents,
        Integer maxRespondents,
        Integer minHighRoleRespondents,
        Integer minOtherCourseRespondents) {
}
