package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RespondentService {
    private final TestRepository testRepository;
    private final ParticipantRepository participantRepository;
    private final EvaluationRepository evaluationRepository;

    public boolean setRespondents(String testId, List<Integer> respondentsIds) {
        EvaluationEntity evaluationEntity = new EvaluationEntity();

        evaluationEntity.setEvaluated(respondentsIds);

        // TODO: add user id for respondent in EvaluationRepository

        evaluationRepository.save(evaluationEntity);

        return true;
    }

    public RespondentsResponseDTO getRespondents(String testId) {
        Optional<TestEntity> testEntity = testRepository.findById(testId);

        if (testEntity.isEmpty()) {
            return null;
        }

        TestEntity test = testEntity.get();
        List<RespondentModel> respondents = new ArrayList<>();

        for (Integer id: test.getParticipantsIds()) {
            Optional<ParticipantEntity> participantEntity = participantRepository.findById(id);

            if (participantEntity.isPresent()) {
                ParticipantEntity participant = participantEntity.get();

                RespondentModel respondent = new RespondentModel(
                        id,
                        participant.getRole().getId(),
                        participant.getFullName(),
                        participant.getCourse());

                respondents.add(respondent);
            }
        }

        return new RespondentsResponseDTO(
                test.getMinRespondents(),
                test.getMaxRespondents(),
                test.getMinHighRoleRespondents(),
                test.getMinHighRoleRespondents(),
                respondents);
    }
}
