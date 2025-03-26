package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.TestEntity;

public interface TestRepository extends MongoRepository<TestEntity, String> {
}
