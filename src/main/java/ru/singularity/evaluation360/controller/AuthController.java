package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.singularity.evaluation360.dto.auth.LoginRequestDTO;
import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;
import ru.singularity.evaluation360.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Вход в систему", description = "Позволяет пользователю войти в систему с использованием имени пользователя и пароля.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(
            @Parameter(description = "Данные для входа: имя пользователя и пароль", required = true)
            @RequestBody LoginRequestDTO login) {
        if (authService.login(login.email(), login.password())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя в системе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных для регистрации")
    })
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @Parameter(description = "Данные для регистрации: имя пользователя, пароль и дополнительные данные", required = true)
            @RequestBody RegisterRequestDTO register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
