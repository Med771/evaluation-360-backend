package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.config.JwtCore;

import ru.singularity.evaluation360.dto.auth.JwtAuthenticationResponseDTO;
import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;

import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;

import ru.singularity.evaluation360.exeptions.FalsiesDtoFormatException;

import ru.singularity.evaluation360.log.annotation.LogEntryExit;
import ru.singularity.evaluation360.log.annotation.LogException;

import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    private final CustomUserDetailsService customUserDetailsService;

    private final PasswordEncoder encoder;

    private final JwtCore jwtCore;

    @LogEntryExit
    @LogException
    public JwtAuthenticationResponseDTO login(String userName, String password) {
        UserEntity userEntity = (UserEntity) customUserDetailsService.loadUserByUsername(userName);

        if (!encoder.matches(password, userEntity.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userEntity,
                null,
                userEntity.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        return new JwtAuthenticationResponseDTO(jwt);
    }

    @LogEntryExit
    @LogException
    public JwtAuthenticationResponseDTO register(RegisterRequestDTO register) {
        if (register == null) {
            throw new FalsiesDtoFormatException("Invalid username or password");
        }

        if (userRepository.existsByEmail(register.email())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserEntity userEntity = new UserEntity();
        ParticipantEntity participantEntity = new ParticipantEntity();

        userEntity.setEmail(register.email());
        userEntity.setPassword(encoder.encode(register.password()));
        userEntity.setRole(RoleUserEnum.USER);

        participantEntity.setFullName(register.fullName());
        participantEntity.setCourse(register.course());

        userEntity.setParticipant(participantEntity);
        participantEntity.setUser(userEntity);

        try {
            participantRepository.save(participantEntity);
            userRepository.save(userEntity);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userEntity,
                null,
                userEntity.getAuthorities());
        return new JwtAuthenticationResponseDTO(jwtCore.generateToken(authentication));
    }
}


