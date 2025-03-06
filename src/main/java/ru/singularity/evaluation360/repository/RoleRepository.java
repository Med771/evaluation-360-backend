package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.RoleEntity;


public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
}
