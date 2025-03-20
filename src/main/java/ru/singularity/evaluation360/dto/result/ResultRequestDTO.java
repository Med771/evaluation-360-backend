package ru.singularity.evaluation360.dto.result;

import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;

import java.util.List;

public record ResultRequestDTO(
        Integer evaluatedId,
        Integer evaluatorId,
        Long startTimeStamp,
        Long endTimeStamp,
        List<AnswerTestModel> answers,
        List<SkillsTestModel> skills) {
}
