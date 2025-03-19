package ru.singularity.evaluation360.dto;

import java.util.List;

public record TestsDTO(
        String nameGroup,
        List<TestTitleDTO> tests) {
}
