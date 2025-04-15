package ru.singularity.evaluation360.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Регистрация пользователя")
public record RegisterRequestDTO(
        @Schema(description = "Имя пользователя")
        String fullName,
        @Schema(description = "Курс пользователя")
        Integer course,
        @Schema(description = "Почта пользователя")
        String email,
        @Schema(description = "Пароль")
        String password) {
}
