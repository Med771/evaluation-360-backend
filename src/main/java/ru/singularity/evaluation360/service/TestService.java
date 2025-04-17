package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.*;

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
    private final ReportRepository reportRepository;

    private List<TestRespondentTitleModel> getEvaluatedRespondents(String testId, int userId, List<Integer> evaluated){
        List<ReportEntity> reportEntities = reportRepository.
                findByTestIdAndEvaluatorIdAndEvaluatedIdIn(testId, userId, evaluated);

        List<Integer> evaluatedIds = reportEntities.stream().map(ReportEntity::getEvaluatedId).toList();

        List<ParticipantEntity> evaluatesComplete = participantRepository.findAllById(evaluatedIds);
        List<ParticipantEntity> evaluatesAll = participantRepository.findAllById(evaluated);

        return generateTestRespondentModels(evaluatesComplete, evaluatesAll);

    }

    private List<TestRespondentTitleModel> generateTestRespondentModels(List<ParticipantEntity> evaluatorsComplete,
                                                                        List<ParticipantEntity> evaluatorsAll){
        Set<ParticipantEntity> incompleteParticipants = new HashSet<>(evaluatorsAll);
        evaluatorsComplete.forEach(incompleteParticipants::remove);
        List<TestRespondentTitleModel> testRespondentTitleModels = new ArrayList<>();

        for (ParticipantEntity participantEntity : evaluatorsComplete) {
            testRespondentTitleModels.add(participantsMapper.
                    toTestRespondentTitleModel(participantEntity, true));
        }

        for (ParticipantEntity participantEntity : incompleteParticipants) {
            testRespondentTitleModels.add(participantsMapper.
                    toTestRespondentTitleModel(participantEntity, false));
        }

        return testRespondentTitleModels;
    }

    private List<TestRespondentTitleModel> getEvaluatorRespondents(String testId, int userId, List<Integer> evaluator){
        List<ReportEntity> reportEntities = reportRepository.
                findByTestIdAndEvaluatorIdInAndEvaluatedId(testId, evaluator, userId);
        List<Integer> evaluatorIds = reportEntities.stream().map(ReportEntity::getEvaluatorId).toList();
        List<ParticipantEntity> evaluatorsComplete = participantRepository.findAllById(evaluatorIds);
        List<ParticipantEntity> evaluatorsAll = participantRepository.findAllById(evaluator);

        return generateTestRespondentModels(evaluatorsComplete, evaluatorsAll);
    }

    private TestResponseDTO toTestResponseDTO(long elevatorId, long evaluatedId, String testId) {
        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new DontFoundException(testId));
        String title = testEntity.getTitle();
        return new TestResponseDTO(title, evaluatedId, elevatorId,
                testMapper.
                        toQuestionTestModelList(questionRepository.
                                findAllById(testEntity.getQuestionsIds())));
    }

    private boolean isAllCompeted(List<TestRespondentTitleModel> testRespondentTitleModels) {
        for(TestRespondentTitleModel testRespondentTitleModel : testRespondentTitleModels){
            if (!testRespondentTitleModel.isComplete()){
                return false;
            }
        }
        return true;
    }

    public TestsResponseDTO getAllTests() {
        List<TestTitleModel> tests = testMapper.toTitleModelList(testRepository.findAll());
        return new TestsResponseDTO("360", tests);
    }

    public TestMenuResponseDTO getTestMenu(String testId, int userId) {
        String index = String.format("%s!_|*|_!%d", testId, userId);
        EvaluationEntity evaluation = evaluationRepository.findByIndex(index)
                .orElseThrow(() -> new DontFoundException(String.format("not found test menu %s", index)));

        List<Integer> evaluatorIds = evaluation.getEvaluator();
        List<Integer> evaluatedIds = evaluation.getEvaluated();

        List<TestRespondentTitleModel> evaluator = new ArrayList<>();
        List<TestRespondentTitleModel> evaluated = new ArrayList<>();
        //TODO не совсем понял какой флаг в TestMenuResponseDTO отвечает за отборожение тех кого мне надо оценить
        boolean isGetRespondents;
        if (!evaluatorIds.isEmpty()){
            evaluator = getEvaluatorRespondents(testId, userId, evaluatorIds);
            isGetRespondents = true;
        } else {
          isGetRespondents = false;
        }

        if (!evaluatedIds.isEmpty()){
            evaluated = getEvaluatorRespondents(testId, userId, evaluatedIds);
        }

        boolean isCompeteEvaluator = isAllCompeted(evaluator);
        boolean isCompeteEvaluated = isAllCompeted(evaluated);



        // TODO получить флаги
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
            ids[entity.getOriginalIndex() - 1] = entity.getId();
        }

        for (Map.Entry<Integer, String> entry : existingIds.entrySet()) {
            ids[entry.getKey() - 1] = entry.getValue();
        }

        return Arrays.asList(ids);
    }


    public TestEntity addTest(TestRequestDTO testRequestDTO){
        return testRepository.save(testMapper.toTestEntity(testRequestDTO, generateQuestionsIds(testRequestDTO)));
    }

    public QuestionsResponseDTO getAllQuestions(){
        return new QuestionsResponseDTO(testMapper.toQuestionModelList(questionRepository.findAll()));
    }
}
