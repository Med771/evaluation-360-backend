package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.WebEntity;

public interface WebRepository extends MongoRepository<WebEntity, Integer> {
}
