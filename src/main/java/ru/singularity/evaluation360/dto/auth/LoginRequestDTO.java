package ru.singularity.evaluation360.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

@Schema(description = "логин")
@ValidateFieldsNotNullOrBlank
public record LoginRequestDTO(
        @Schema(description = "почта пользователя")
        @Email
        String email,
        @Schema(description = "пароль пользователя")
        @Size(min = 8, max = 30)
        @Pattern(
                regexp = "^[A-Za-z\\d@$!%*?&]{8,30}$",
                message = "Пароль должен содержать от 8 до 30 символов, хотя бы одну заглавную букву, строчную, цифру и спецсимвол"
        )
        String password) {
}
