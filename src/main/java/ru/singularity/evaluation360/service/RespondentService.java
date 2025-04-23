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
import ru.singularity.evaluation360.exeptions.RepeatException;

import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RespondentService {
    private final String splitter;

    private final TestRepository testRepository;
    private final ParticipantRepository participantRepository;
    private final EvaluationRepository evaluationRepository;

    private List<EvaluationEntity> generateAndAppendUser(int userId, String testId, List<Integer> respondentsIds, List<EvaluationEntity> evaluationEntities) {
        // Перевод массива сущностей в Map<Индекс; сущность>
        Map<String, EvaluationEntity> evaluationEntityMap = evaluationEntities.stream().collect(Collectors.toMap(EvaluationEntity::getIndex, e -> e));

        // Новый массив сущностей для сохранения
        List<EvaluationEntity> newEvaluationEntities = new ArrayList<>();

        // Множество для индексов
        Set<String> evaluationIndexes = new HashSet<>();

        // Индекс для пользователя
        String myIndex = testId + splitter + userId;

        // Переменная для работы с сущностью
        EvaluationEntity evaluationEntity = evaluationEntityMap.getOrDefault(myIndex, new EvaluationEntity());

        // Проверка на сущствование индекса пользователя
        if(evaluationEntity.getIndex() == null){
            evaluationEntity.setIndex(testId + splitter + userId);
        }

        // Обновление сущности с пользователем
        evaluationEntity.setEvaluated(respondentsIds);
        evaluationIndexes.add(myIndex);
        newEvaluationEntities.add(evaluationEntity);

        for (Integer respondentId : respondentsIds) {
            String index = testId + splitter + respondentId;

            if (evaluationIndexes.add(index)) {
               evaluationEntity = evaluationEntityMap.getOrDefault(index, new EvaluationEntity());

                if (evaluationEntity.getIndex() == null) {
                    evaluationEntity.setIndex(index);
                }

                evaluationEntity.getEvaluator().add(userId);
                newEvaluationEntities.add(evaluationEntity);
            }
        }

        return newEvaluationEntities;
    }

    public void setRespondents(Integer userId, String testId, RespondentsRequestDTO respondentsRequestDTO) {
        EvaluationEntity evaluationEntity = evaluationRepository.findByIndex(testId+splitter+userId).orElse(null);
        if(evaluationEntity != null && !evaluationEntity.getEvaluated().isEmpty()){
            throw new RepeatException("you repeat self-evaluation");
        }

        List<Integer> respondentsIds = respondentsRequestDTO.respondentsIds();

        List<EvaluationEntity> evaluations = evaluationRepository.findAllByIndexIsStartingWith(testId);

        evaluationRepository.saveAll(this.generateAndAppendUser(userId, testId, respondentsIds, evaluations));
    }

    public RespondentsResponseDTO getRespondents(String testId) {
        Optional<TestEntity> testEntity = testRepository.findById(testId);
        TestEntity test = testEntity.orElseThrow(() -> new DontFoundException("Test not found"));

        List<RespondentModel> respondents = new ArrayList<>();

        // NOTE: delete Participant
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
