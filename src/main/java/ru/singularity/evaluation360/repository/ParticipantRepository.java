package ru.singularity.evaluation360.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.singularity.evaluation360.entity.ParticipantEntity;

import java.util.List;


public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Integer> {
}
