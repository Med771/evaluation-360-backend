package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.ReportEntity;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {
}
