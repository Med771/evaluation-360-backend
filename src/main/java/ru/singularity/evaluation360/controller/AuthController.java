package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import ru.singularity.evaluation360.dto.auth.LoginRequestDTO;
import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;

import ru.singularity.evaluation360.service.AuthService;

import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

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
            @RequestBody LoginRequestDTO login,
            HttpServletResponse response) {
        String token = authService.login(login.email(), login.password());

        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(3600)

                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя в системе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных для регистрации")
    })
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @Parameter(description = "Данные для регистрации: имя пользователя, пароль и дополнительные данные", required = true)
            @RequestBody RegisterRequestDTO register,
            HttpServletResponse response) {
        String token = authService.register(register);

        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(3600)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @Operation(summary = "Получить CSRF токен", description = "Возвращает новый CSRF токен для авторизованного пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение токена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/csrf-token")
    public ResponseEntity<HttpStatus> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            String token = csrf.getToken();

            ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", token)
                    .httpOnly(false)
                    .secure(false)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(3600)
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            response.setHeader("X-CSRF-TOKEN", token);
            response.setHeader("X-XSRF-TOKEN", token);
            response.setHeader("X-CSRF-HEADER", csrf.getHeaderName());
            response.setHeader("X-CSRF-PARAM", csrf.getParameterName());
        }
        
        return ResponseEntity.ok().body(HttpStatus.OK);
    }
}
