package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.EvaluationEntity;

public interface EvaluationRepository extends MongoRepository<EvaluationEntity, String> {
}
