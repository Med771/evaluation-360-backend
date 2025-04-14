package ru.singularity.evaluation360.dto.auth;

public record RegisterRequestDTO(
        String fullName,
        Integer course,
        Integer roleId,
        String email,
        String password) {
}
