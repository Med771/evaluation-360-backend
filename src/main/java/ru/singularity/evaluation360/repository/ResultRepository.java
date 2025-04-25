package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.ResultEntity;

import java.util.Optional;

public interface ResultRepository extends MongoRepository<ResultEntity, String> {
    Optional<ResultEntity> findByUserTestIndex(String userTestIndex);
}
