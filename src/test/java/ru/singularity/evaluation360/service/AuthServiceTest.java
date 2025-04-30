package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String email = "test@example.com";
    private final String password = "password";

    private final UserEntity user = new UserEntity();
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        user.setEmail(email);
        user.setPassword("encodedPassword");

        registerRequest = new RegisterRequestDTO(
                "Test User",
                1,
                "test@example.com",
                "password"
        );
    }

    @Test
    void login_Success() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean result = authService.login(email, password);

        assertTrue(result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = authService.login(email, password);

        assertFalse(result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_WrongPassword() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        boolean result = authService.login(email, password);

        assertFalse(result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, user.getPassword());
    }

    @Test
    void register_Success() {

        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(participantRepository.save(any(ParticipantEntity.class))).thenReturn(new ParticipantEntity());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        boolean result = authService.register(registerRequest);

        assertTrue(result);
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(participantRepository).save(any(ParticipantEntity.class));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_EmailAlreadyExists() {

        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        boolean result = authService.register(registerRequest);

        assertFalse(result);
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(passwordEncoder, never()).encode(any());
        verify(participantRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_NullRequest() {
        boolean result = authService.register(null);

        assertFalse(result);
        verify(userRepository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(participantRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserByEmail_Success() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserEntity result = authService.findUserByEmail(email);

        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findUserByEmail_UserNotFound() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.findUserByEmail(email));
        verify(userRepository).findByEmail(email);
    }
} 