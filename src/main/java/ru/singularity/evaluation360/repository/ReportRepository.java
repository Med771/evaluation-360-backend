package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.ReportEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {
    List<ReportEntity> findByTestIdAndIndexIn(String testId, Collection<String> index);
    List<ReportEntity> findAllByTestId(String testId);
    Optional<ReportEntity> findByIndex(String evaluatedIdTestIdEvaluatorId);

    boolean existsByIndex(String evaluatedIdTestIdEvaluatorId);
}
