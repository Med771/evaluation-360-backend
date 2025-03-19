package ru.singularity.evaluation360.dto;

public record TestTitleDTO(
        Long pk,
        String title,
        Long startTimeStamp,
        Long endTimeStamp) {
}
