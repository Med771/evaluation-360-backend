package ru.singularity.evaluation360.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.RoleRepository;
import ru.singularity.evaluation360.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ParticipantRepository participantRepository;

    private final PasswordEncoder encoder;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ParticipantRepository participantRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.participantRepository = participantRepository;
        this.encoder = encoder;
    }

    public boolean login(String userName, String password) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(userName);

        if (userEntity.isEmpty()) {
            return false;
        }

        return userEntity.filter(entity -> encoder.matches(password, entity.getPassword())).isPresent();
    }

    public boolean register(RegisterRequestDTO register) {
        if (register == null) {
            return false;
        }

        if (userRepository.existsByEmail(register.email())) {
            return false;
        }

        if (!roleRepository.existsById(register.roleId())) {
            return false;
        }

        UserEntity userEntity = new UserEntity();
        ParticipantEntity participantEntity = new ParticipantEntity();

        userEntity.setEmail(register.email());
        userEntity.setPassword(encoder.encode(register.password()));

        participantEntity.setFullName(register.fullName());
        participantEntity.setCourse(register.course());
        participantEntity.setRole(roleRepository.getReferenceById(register.roleId()));

        userEntity.setParticipant(participantEntity);
        participantEntity.setUser(userEntity);

        participantRepository.save(participantEntity);
        userRepository.save(userEntity);

        return true;
    }}
