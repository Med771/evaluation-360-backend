package ru.singularity.evaluation360.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

@Schema(description = "Регистрация пользователя")
@ValidateFieldsNotNullOrBlank
public record RegisterRequestDTO(
        @Schema(description = "Имя пользователя")
        String fullName,
        @Schema(description = "Курс пользователя")
        @Max(4)
        @Min(-1)
        Integer course,
        @Schema(description = "Почта пользователя")
        @Email
        String email,
        @Schema(description = "Пароль")
        @Pattern(
                regexp = "^[A-Za-z\\d@$!%*?&]{8,30}$",
                message = "Пароль должен содержать от 8 до 30 символов, хотя бы одну заглавную букву, строчную, цифру и спецсимвол"
        )
        String password) {
}
