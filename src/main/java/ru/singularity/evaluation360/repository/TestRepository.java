package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;

import java.util.List;

public interface TestRepository extends MongoRepository<TestEntity, String> {
    List<TestEntity> findByParticipantsIdsContaining(Integer id);
    List<TestEntity> findAllByStatus(StatusTestEnum status);
}
