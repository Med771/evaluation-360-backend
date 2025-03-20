package ru.singularity.evaluation360.dto.test.model;

import java.util.List;

public record QuestionTestModel(
        String questionText,
        List<Integer> skills) {
}
