package ru.singularity.evaluation360.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.singularity.evaluation360.entity.QuestionEntity;


public interface QuestionRepository extends MongoRepository<QuestionEntity, String> {
}
