package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;

import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.exeptions.BadRequestException;

import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.mapper.QuestionMapper;
import ru.singularity.evaluation360.mapper.SkillMapper;
import ru.singularity.evaluation360.mapper.TestMapper;

import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.QuestionRepository;
import ru.singularity.evaluation360.repository.SkillRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestManagementService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ParticipantRepository participantRepository;
    private final SkillRepository skillRepository;

    private final TestMapper testMapper;
    private final QuestionMapper questionMapper;
    private final SkillMapper skillMapper;
    private final ParticipantsMapper participantsMapper;

    private TestResponseDTO toTestResponseDTO(long elevatorId, long evaluatedId, String testId) {
        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new BadRequestException(testId));
        String title = testEntity.getTitle();
        return new TestResponseDTO(title, evaluatedId, elevatorId,
                questionMapper.
                        toQuestionTestModelList(questionRepository.
                                findAllById(testEntity.getQuestionsIds())));
    }

    /**
     * составление правильного порядка вопросов с сохранением их
     * @param testRequestDTO дто в котором есть вопросы
     * @return лист с id вопросов в правильном порядке
     */
    private List<String> generateQuestionsIds(TestRequestDTO testRequestDTO) {

        // составляем массив в котором будем располагать вопросы
        int totalSize = testRequestDTO.questionIds().size() + testRequestDTO.newQuestions().size();
        String[] ids = new String[totalSize];

        // делаем 2 мапы первая с новыми вопросами вторая с уже используемыми
        Map<Integer, QuestionTestModel> newQuestions = testRequestDTO.newQuestions();
        Map<Integer, String> existingIds = testRequestDTO.questionIds();

        // список для сохранения пакетом
        List<QuestionEntity> entitiesToSave = new ArrayList<>();
        // проходимся по мапе попутно заполняя временное поле которое указывает порядковый номер
        for (Map.Entry<Integer, QuestionTestModel> entry : newQuestions.entrySet()) {
            QuestionEntity entity = questionMapper.toQuestionEntity(entry.getValue());
            entity.setOriginalIndex(entry.getKey());
            entitiesToSave.add(entity);
        }

        // сохраняем
        List<QuestionEntity> savedEntities = questionRepository.saveAll(entitiesToSave);

        savedEntities.sort(Comparator.comparingInt(QuestionEntity::getOriginalIndex));

        // 2 цикла по старым и новым id которые выставляют их в списке по их порядковому номеру
        for (QuestionEntity entity : savedEntities) {
            ids[entity.getOriginalIndex() - 1] = entity.getId();
        }

        for (Map.Entry<Integer, String> entry : existingIds.entrySet()) {
            ids[entry.getKey() - 1] = entry.getValue();
        }

        return Arrays.asList(ids);
    }

    public void editTestStatus(String testID, TestStatusRequestDTO testStatusRequestDTO){
        TestEntity testEntity = testRepository.findById(testID).orElseThrow(() -> new BadRequestException(testID));
        testEntity.setStatus(testStatusRequestDTO.status());
        testRepository.save(testEntity);
    }

    public TestsResponseDTO getAllTests(Integer userId) {
        List<TestTitleModel> tests = testMapper.toTitleModelList(testRepository.findByParticipantsIdsContaining(userId));
        return new TestsResponseDTO("360", tests);
    }

    public TestResponseDTO getTest(String testID, long userId, long evaluatedId) {
        return toTestResponseDTO(userId, evaluatedId, testID);
    }

    public TestViewResponseDTO getTest(String testID) {
        TestEntity testEntity = testRepository.findById(testID).orElseThrow(() -> new BadRequestException(testID));
        return testMapper.toTestViewResponseDTO(testEntity,
                participantsMapper.
                        toRespondentModels(participantRepository.findAllById(testEntity.getParticipantsIds())),
                questionMapper.toQuestionModelList(questionRepository.findAllById(testEntity.getQuestionsIds())));
    }

    public void addTest(TestRequestDTO testRequestDTO) {
        skillRepository.saveAll(skillMapper.toSkillsEntity(testRequestDTO.newSkills()));
        testRepository.save(testMapper.toTestEntity(testRequestDTO,
                generateQuestionsIds(testRequestDTO),
                StatusTestEnum.CREATED));
    }
}
