package ru.singularity.evaluation360.dto.result;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentEditRequestDTO(
        @Schema(description = "новый комментарий")
        String newComment) {
}
