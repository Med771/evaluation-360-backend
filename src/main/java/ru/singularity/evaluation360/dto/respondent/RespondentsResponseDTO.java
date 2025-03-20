package ru.singularity.evaluation360.dto.respondent;

import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;

import java.util.List;

public record RespondentsResponseDTO(
        Integer minRespondents,
        Integer maxRespondents,
        Integer minHighRoleRespondents,
        Integer minOtherCourseRespondents,
        List<RespondentModel> respondents) {
}
