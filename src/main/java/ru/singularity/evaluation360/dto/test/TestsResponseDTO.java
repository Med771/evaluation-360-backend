package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.test.model.TestTitleModel;

import java.util.List;

public record TestsResponseDTO(
        String nameGroup,
        List<TestTitleModel> tests) {
}
