package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.AnswerEntity;


public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {
}
