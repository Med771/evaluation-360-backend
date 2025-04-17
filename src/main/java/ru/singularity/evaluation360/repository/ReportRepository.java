package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.ReportEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {
    List<ReportEntity> findByTestIdAndEvaluatorIdAndEvaluatedIdIn(String testId, Integer evaluatorId, Collection<Integer> evaluatedId);
    List<ReportEntity> findByTestIdAndEvaluatorIdInAndEvaluatedId(String testId, Collection<Integer> evaluatorId, Integer evaluatedId);
    Optional<ReportEntity> findByEvaluatedIdTestIdEvaluatorId(String evaluatedIdTestIdEvaluatorId);
}
