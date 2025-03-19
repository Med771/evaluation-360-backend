package ru.singularity.evaluation360.dto;

import java.util.List;

public record TestMenuDTO(
        String title,
        Boolean isGetRespondents,
        Boolean isSelectRespondents,
        Boolean isCompleteEvaluation,
        List<RespondentTitleDTO> evaluatedRespondents,
        List<RespondentTitleDTO> evaluatorRespondents,
        Boolean isActiveResult) {
}
