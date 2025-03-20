package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;

import java.util.List;

public record TestMenuResponseDTO(
        String title,
        Boolean isGetRespondents,
        Boolean isSelectRespondents,
        Boolean isCompleteEvaluation,
        Boolean isCompleteEvaluated,
        Boolean isCompeteEvaluator,
        List<TestRespondentTitleModel> evaluatedRespondents,
        List<TestRespondentTitleModel> evaluatorRespondents,
        Boolean isActiveResult) {
}
