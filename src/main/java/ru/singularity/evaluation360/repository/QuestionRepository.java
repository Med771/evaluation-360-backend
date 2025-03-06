package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.QuestionEntity;


public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {
}
