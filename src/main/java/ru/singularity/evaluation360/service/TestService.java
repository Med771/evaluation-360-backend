package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.QuestionRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final EvaluationRepository evaluationRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantsMapper participantsMapper;
    private final QuestionRepository questionRepository;

    private boolean isCompleteTest(int elevatorId, String testId, int evaluatedId) {
        //TODO придумать способ узнавать выполнил ли тест пользователь
        return true;
    }

    private TestResponseDTO toTestResponseDTO(long elevatorId, long evaluatedId, String testId) {
        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new DontFoundException(testId));
        String title = testEntity.getTitle();
        return new TestResponseDTO(title, evaluatedId, elevatorId,
                testMapper.
                        toQuestionTestModelList(questionRepository.
                                findAllById(testEntity.getQuestionsIds())));
    }

    public TestsResponseDTO getAllTests() {
        List<TestTitleModel> tests = testMapper.toTitleModelList(testRepository.findAll());
        return new TestsResponseDTO("360", tests);
    }

    public TestMenuResponseDTO getTestMenu(String id, int userId) {
        String index = String.format("%d!_|*|_!%s", userId, id);
        EvaluationEntity evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new DontFoundException(String.format("not found test menu %s", index)));


        // TODO получить флаги
        // TODO получить дто TestRespondentTitleModel и переоброзовать через маппер
        return null;
    }

    public TestResponseDTO getTest(String testID, long userId, long evaluatedId) {
        return toTestResponseDTO(userId, evaluatedId, testID);
    }

    public TestViewResponseDTO getTest(String testID) {
        TestEntity testEntity = testRepository.findById(testID).orElseThrow(() -> new DontFoundException(testID));
        return testMapper.toTestViewResponseDTO(testEntity,
                participantsMapper.
                        toRespondentModels(participantRepository.findAllById(testEntity.getParticipantsIds())),
                testMapper.toQuestionModelList(questionRepository.findAllById(testEntity.getQuestionsIds())));
    }

    private List<String> generateQuestionsIds(TestRequestDTO testRequestDTO) {
        int totalSize = testRequestDTO.questionIds().size() + testRequestDTO.newQuestions().size();
        String[] ids = new String[totalSize];

        Map<Integer, QuestionTestModel> newQuestions = testRequestDTO.newQuestions();
        Map<Integer, String> existingIds = testRequestDTO.questionIds();

        List<QuestionEntity> entitiesToSave = new ArrayList<>();
        for (Map.Entry<Integer, QuestionTestModel> entry : newQuestions.entrySet()) {
            QuestionEntity entity = testMapper.toQuestionEntity(entry.getValue());
            entity.setOriginalIndex(entry.getKey());
            entitiesToSave.add(entity);
        }

        List<QuestionEntity> savedEntities = questionRepository.saveAll(entitiesToSave);

        savedEntities.sort(Comparator.comparingInt(QuestionEntity::getOriginalIndex));

        for (QuestionEntity entity : savedEntities) {
            ids[entity.getOriginalIndex()] = entity.getId();
        }

        for (Map.Entry<Integer, String> entry : existingIds.entrySet()) {
            ids[entry.getKey()] = entry.getValue();
        }

        return Arrays.asList(ids);
    }


    public TestEntity addTest(TestRequestDTO testRequestDTO){
        return testRepository.save(testMapper.toTestEntity(testRequestDTO, generateQuestionsIds(testRequestDTO)));
    }
}
