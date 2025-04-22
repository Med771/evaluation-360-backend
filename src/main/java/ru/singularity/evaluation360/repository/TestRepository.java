package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.TestEntity;

import java.util.List;

public interface TestRepository extends MongoRepository<TestEntity, String> {
    List<TestEntity> findByParticipantsIdsContaining(Integer id);
}
