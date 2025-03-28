package ru.singularity.evaluation360.dto.test.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record QuestionModel(
        String id,

        @Schema(description = "Текст вопроса", example = "Каковы ваши сильные стороны?")
        String questionText,

        @Schema(description = "Список идентификаторов навыков", example = "[1, 2, 3]")
        List<Integer> skillsIds) {
}
