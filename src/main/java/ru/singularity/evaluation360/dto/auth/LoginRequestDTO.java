package ru.singularity.evaluation360.dto.auth;

public record LoginRequestDTO(
        String email,
        String password) {
}
