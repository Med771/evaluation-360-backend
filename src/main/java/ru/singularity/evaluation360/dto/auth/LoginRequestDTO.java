package ru.singularity.evaluation360.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "логин")
public record LoginRequestDTO(
        @Schema(description = "почта пользователя")
        String email,
        @Schema(description = "пароль пользователя")
        String password) {
}
