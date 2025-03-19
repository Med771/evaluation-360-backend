package ru.singularity.evaluation360.dto;

import java.util.List;

public record TestDTO(
        String title,
        Long evaluatedId,
        Long evaluatorId,
        List<QuestionDTO> questionDTOS) {
}
