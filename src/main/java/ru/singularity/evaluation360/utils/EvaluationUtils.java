package ru.singularity.evaluation360.utils;

import ru.singularity.evaluation360.entity.ParticipantEntity;

import java.util.List;

public record EvaluationUtils(List<ParticipantEntity> evaluatesComplete, List<ParticipantEntity> evaluatesAll) {
}
