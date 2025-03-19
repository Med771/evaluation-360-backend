package ru.singularity.evaluation360.dto;

import java.util.List;

public record QuestionDTO(
        String questionText,
        List<Integer> skills) {
}
