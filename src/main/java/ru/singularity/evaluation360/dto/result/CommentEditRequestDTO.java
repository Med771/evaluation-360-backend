package ru.singularity.evaluation360.dto.result;

public record CommentEditRequestDTO(
        Integer skillIndex,
        Integer commentIndex,
        String newComment) {
}
