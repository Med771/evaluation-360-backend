package ru.singularity.evaluation360.dto.result;

import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;

import java.util.List;

public record ResultRequestDTO(
        Integer evaluatedId,
        Integer evaluatorId,
        Long startTimeStamp,
        Long endTimeStamp,
        List<AnswerTestModel> answers) {
}
