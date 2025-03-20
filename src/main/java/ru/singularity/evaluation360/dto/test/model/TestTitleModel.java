package ru.singularity.evaluation360.dto.test.model;

public record TestTitleModel(
        Long testId,
        String title,
        Long startTimeStamp,
        Long endTimeStamp) {
}
