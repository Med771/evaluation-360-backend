package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.test.model.QuestionModel;

import java.util.List;

public record QuestionResponseDTO(
        List<QuestionModel> questions) {
}
