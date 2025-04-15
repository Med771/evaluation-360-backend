package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.EvaluationEntity;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends MongoRepository<EvaluationEntity, String> {
    Optional<EvaluationEntity> findByIndex(String index);

    List<EvaluationEntity> findAllByIndexIsStartingWith(String testId);
}
