package ru.singularity.evaluation360.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.auth.RegisterRequestDTO;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    private final PasswordEncoder encoder;

    public AuthService(
            UserRepository userRepository,
            ParticipantRepository participantRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
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
            return false;
        }

        return true;
    }

    public UserEntity findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new DontFoundException("Don't found user"));
    }

}


