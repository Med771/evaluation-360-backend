package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.respondent.RespondentsRequestDTO;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RespondentService {
    private static final String splitter = "!_!*!_!";

    private final TestRepository testRepository;
    private final ParticipantRepository participantRepository;
    private final EvaluationRepository evaluationRepository;

    private List<EvaluationEntity> generateAndAppendUser(int userId, String testId, List<Integer> respondentsIds, List<EvaluationEntity> evaluationEntities) {
        evaluationEntities.sort(
                (evaluation1, evaluation2) ->
                        Integer.compare(
                                Integer.parseInt(evaluation1.getIndex().split(splitter)[1]),
                                Integer.parseInt(evaluation2.getIndex().split(splitter)[1])
                        ));

        List<EvaluationEntity> newEvaluationEntities = new ArrayList<>();

        int i1 = 0;
        int i2 = 0;

        while (i2 < evaluationEntities.size()) {
            //TODO возникает ошибка Index Error ru.singularity.evaluation360.service.RespondentService.generateAndAppendUser(RespondentService.java:44)
            if (respondentsIds.get(i1) == Integer.parseInt(evaluationEntities.get(i2).getIndex().split(splitter)[1])) {
                evaluationEntities.get(i2).getEvaluated().add(userId);

                i1++;
                i2++;

                newEvaluationEntities.add(evaluationEntities.get(i2));
            }
            else{
                EvaluationEntity evaluationEntity = new EvaluationEntity();

                evaluationEntity.setIndex(testId + splitter + userId);
                evaluationEntity.getEvaluated().add(userId);

                i1++;

                newEvaluationEntities.add(evaluationEntity);
            }
        }

        while (i1 < respondentsIds.size()) {
            EvaluationEntity evaluationEntity = new EvaluationEntity();

            evaluationEntity.setIndex(testId + splitter + userId);
            evaluationEntity.getEvaluated().add(userId);

            newEvaluationEntities.add(evaluationEntity);

            i1++;
        }

        return newEvaluationEntities;
    }

    public void setRespondents(Integer userId, String testId, RespondentsRequestDTO respondentsRequestDTO) {
        String index = testId + splitter + userId;

        List<Integer> respondentsIds = respondentsRequestDTO.respondentsIds();

        Optional<EvaluationEntity> evaluationEntity = evaluationRepository.findByIndex(index);

        EvaluationEntity evaluation;

        if (evaluationEntity.isPresent()) {
            evaluation = evaluationEntity.get();
        }
        else {
            evaluation = new EvaluationEntity();

            evaluation.setIndex(index);
        }

        evaluation.setEvaluator(respondentsIds);

        List<EvaluationEntity> evaluations = evaluationRepository.findAllByIndexIsStartingWith(testId);

        evaluationRepository.saveAll(this.generateAndAppendUser(userId, testId, respondentsIds, evaluations));
    }

    public RespondentsResponseDTO getRespondents(String testId) {
        Optional<TestEntity> testEntity = testRepository.findById(testId);
        TestEntity test = testEntity.orElseThrow(() -> new DontFoundException("Test not found"));

        List<RespondentModel> respondents = new ArrayList<>();

        List<ParticipantEntity> participants = participantRepository.findAllById(test.getParticipantsIds());

        for (ParticipantEntity participant: participants) {
            RespondentModel respondent = new RespondentModel(
                    participant.getId(),
                    participant.getUser().getRole(),
                    participant.getFullName(),
                    participant.getCourse());

            respondents.add(respondent);
        }

        return new RespondentsResponseDTO(
                test.getMinRespondents(),
                test.getMaxRespondents(),
                test.getMinHighRoleRespondents(),
                test.getMinHighRoleRespondents(),
                respondents);
    }
}
