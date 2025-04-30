package ru.singularity.evaluation360.config;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;

import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.UserRepository;

@Configuration
@AllArgsConstructor
public class DataInitializerConfig {
    private final PasswordEncoder passwordEncoder;

    @Value("${adminstrator.config.email}")
    private String adminEmail;

    @Value("${adminstrator.config.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initUser(UserRepository userRepository, ParticipantRepository participantRepository) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                UserEntity userEntity = new UserEntity();
                ParticipantEntity participantEntity = new ParticipantEntity();

                userEntity.setEmail(adminEmail);
                userEntity.setPassword(passwordEncoder.encode(adminPassword));
                userEntity.setRole(RoleUserEnum.ADMIN);

                participantEntity.setFullName("ADMIN");
                participantEntity.setCourse(-1);

                userEntity.setParticipant(participantEntity);
                participantEntity.setUser(userEntity);

                participantRepository.save(participantEntity);
                userRepository.save(userEntity);
            }
        };
    }
}
