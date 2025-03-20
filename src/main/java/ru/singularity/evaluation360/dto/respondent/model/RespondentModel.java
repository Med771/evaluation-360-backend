package ru.singularity.evaluation360.dto.respondent.model;

public record RespondentModel(
        Long respondentId,
        Integer roleId,
        String fullName,
        Integer course) {
}
