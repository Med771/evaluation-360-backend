package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.ParticipantEntity;


public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Integer> {
}
