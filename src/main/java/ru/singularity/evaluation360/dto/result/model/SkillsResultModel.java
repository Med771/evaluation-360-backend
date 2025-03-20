package ru.singularity.evaluation360.dto.result.model;

import java.util.List;

public record SkillsResultModel(
        String skillName,
        Double thisEvaluation,
        Double commandEvaluation,
        Double expertEvaluation,
        List<String> comments) {
}
