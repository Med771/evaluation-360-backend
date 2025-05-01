package ru.singularity.evaluation360.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import ru.singularity.evaluation360.config.JwtCore;
import ru.singularity.evaluation360.dto.auth.LoginRequestDTO;
import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.CustomUserDetailsService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.secret.token=797169816cebd7bfaa55fb8c2fhkfkvkb595be6eb9d40ytf7219f37b9803balt",
        "jwt.time.live=360000"
})
class AuthControllerTest extends BaseControllerTest {

    @MockBean
    private AuthService authService;





    @Test
    void login_Success() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        when(authService.login(loginRequest.email(), loginRequest.password())).thenReturn("jwt");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void login_Failure() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrongpassword");

        doThrow(BadCredentialsException.class).when(authService).login(loginRequest.email(), loginRequest.password());

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "Test User",
                1,
                "test@example.com",
                "password"
        );

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn("jwt");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void register_Failure() throws Exception {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                "Test User",
                1,
                "existing@example.com",
                "password"
        );

        doThrow(BadCredentialsException.class).when(authService).register(any(RegisterRequestDTO.class));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isUnauthorized());
    }
}
