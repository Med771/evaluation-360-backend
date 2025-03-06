package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.SkillEntity;


public interface SkillRepository extends JpaRepository<SkillEntity, Integer> {
}
