package ru.singularity.evaluation360.dto;

import java.util.List;

public record ResultDTO(
        String title,
        Double thisResult,
        Double commandResult,
        Double expertResult,
        List<SkillsResultDTO> results,
        String resultComment) {
}
