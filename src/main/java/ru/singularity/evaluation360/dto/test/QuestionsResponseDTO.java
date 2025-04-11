package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;

import java.util.List;

@Schema(description = "существующие вопросы")
public record QuestionsResponseDTO(
        List<QuestionModel> questions) {
}
