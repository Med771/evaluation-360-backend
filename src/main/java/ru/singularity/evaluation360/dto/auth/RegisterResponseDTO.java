package ru.singularity.evaluation360.dto.auth;

public record RegisterResponseDTO(
        String fullName,
        String course,
        String roleId,
        String email,
        String password) {
}
