package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;

import java.util.List;

public record TestResponseDTO(
        String title,
        Long evaluatedId,
        Long evaluatorId,
        List<QuestionTestModel> questions) {
}
