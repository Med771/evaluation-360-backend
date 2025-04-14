package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    public boolean existsByEmail(String email);
}
