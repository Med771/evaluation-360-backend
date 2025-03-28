package ru.singularity.evaluation360.dto.auth;

public record RegisterRequestDTO(
        String fullName,
        String course,
        String roleId,
        String email,
        String password) {
}
