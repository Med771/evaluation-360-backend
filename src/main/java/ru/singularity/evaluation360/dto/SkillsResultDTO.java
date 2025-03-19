package ru.singularity.evaluation360.dto;

import java.util.List;

public record SkillsResultDTO(
        String skillName,
        Double thisEvaluation,
        Double commandEvaluation,
        Double expertEvaluation,
        List<String> comments) {
}
