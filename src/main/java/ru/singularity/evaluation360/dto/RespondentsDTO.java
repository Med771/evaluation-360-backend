package ru.singularity.evaluation360.dto;

import java.util.List;

public record RespondentsDTO(
        Integer minRespondents,
        Integer maxRespondents,
        Integer minHighRoleRespondents,
        Integer minOtherCourseRespondents,
        List<RespondentDTO> respondents) {
}
