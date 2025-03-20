package ru.singularity.evaluation360.dto.result;

import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;

import java.util.List;

public record ResultResponseDTO(
        String title,
        Double thisResult,
        Double commandResult,
        Double expertResult,
        List<SkillsResultModel> results,
        String resultComment) {
}
