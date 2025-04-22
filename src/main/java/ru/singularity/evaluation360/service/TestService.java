package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestMapper testMapper;
    private final ParticipantsMapper participantsMapper;

    private final TestRepository testRepository;
    private final EvaluationRepository evaluationRepository;
    private final ParticipantRepository participantRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final SkillRepository skillRepository;

    private final String splitter;

    /**
     * метод для нахождения TestRespondentTitleModel с полем оценил ли его пользователь
     * @param testId id теста
     * @param userId id пользователя
     * @param evaluated список id тех кого должен оценить пользователь
     * @return список TestRespondentTitleModel с полем оценил ли он пользователя
     */
    private List<TestRespondentTitleModel> getEvaluatedRespondents(String testId, int userId, List<Integer> evaluated) {
        // нахождение всех репортов которые соответствуют условию
        // testId == testId and elevatorId == userId and evaluatedId in evaluated
        // для того чтобы найти репорты в которых пользователь оценил кого то и далее вытянуть из них кого он оценил
        List<ReportEntity> reportEntities = reportRepository.
                findByTestIdAndEvaluatorIdAndEvaluatedIdIn(testId, userId, evaluated);

        // список id кого пользователь оценил
        List<Integer> evaluatedIds = reportEntities.stream().map(ReportEntity::getEvaluatedId).toList();


        // поиск ParticipantEntity которых оценил пользователь и всех
        List<ParticipantEntity> evaluatesComplete = participantRepository.findAllById(evaluatedIds);
        List<ParticipantEntity> evaluatesAll = participantRepository.findAllById(evaluated);

        return generateTestRespondentModels(evaluatesComplete, evaluatesAll);

    }

    /**
     * метод для нахождения кто оценил кого оценил а кто кого не оценил
     * @param evaluatorsComplete те кто оценили кого то или их оценили
     * @param evaluatorsAll все кого должны оценить или те кто должен оценить
     * @return список TestRespondentTitleModel где указано выполнил ли он оценку
     */
    private List<TestRespondentTitleModel> generateTestRespondentModels(List<ParticipantEntity> evaluatorsComplete,
                                                                        List<ParticipantEntity> evaluatorsAll) {

        // создания сета для дальнейшего нахождения всех кто не оценил
        Set<ParticipantEntity> incompleteParticipants = new HashSet<>(evaluatorsAll);
        evaluatorsComplete.forEach(incompleteParticipants::remove);

        // создания списка для добавления
        List<TestRespondentTitleModel> testRespondentTitleModels = new ArrayList<>();

        // те кто оценил
        for (ParticipantEntity participantEntity : evaluatorsComplete) {
            testRespondentTitleModels.add(participantsMapper.
                    toTestRespondentTitleModel(participantEntity, true));
        }

        // те кто не оценил
        for (ParticipantEntity participantEntity : incompleteParticipants) {
            testRespondentTitleModels.add(participantsMapper.
                    toTestRespondentTitleModel(participantEntity, false));
        }

        return testRespondentTitleModels;
    }

    /**
     * метод для нахождения TestRespondentTitleModel с полем оценил ли он пользователя
     * @param testId тест id
     * @param userId id пользователя
     * @param evaluator id оценщиков
     * @return список TestRespondentTitleModel с указанием оценил ли он пользователя
     */
    private List<TestRespondentTitleModel> getEvaluatorRespondents(String testId, int userId, List<Integer> evaluator) {
        // нахождение всех репортов которые соответствуют условию
        // testId == testId and elevatorId in evaluator and evaluatedId == userId
        // для того чтобы найти репорты в которых пользователя оценили
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
        for (TestRespondentTitleModel testRespondentTitleModel : testRespondentTitleModels) {
            if (!testRespondentTitleModel.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public TestsResponseDTO getAllTests() {
        List<TestTitleModel> tests = testMapper.toTitleModelList(testRepository.findAll());
        return new TestsResponseDTO("360", tests);
    }

    /**
     * получение и формирование TestMenu
     * @param testId id теста
     * @param userId id пользователя
     * @return TestMenuResponseDTO
     */
    public TestMenuResponseDTO getTestMenu(String testId, int userId) {
        // формирование составного индекса и получение по нему EvaluationEntity
        String index = String.format("%s%s%d", testId, splitter, userId);
        Optional<EvaluationEntity> evaluation = evaluationRepository.findByIndex(index);

        List<Integer> evaluatorIds;
        List<Integer> evaluatedIds;


        if(evaluation.isPresent()) {
            // списки id тех кто оценивает и тех кого оценивают
            evaluatorIds = evaluation.get().getEvaluator();
            evaluatedIds = evaluation.get().getEvaluated();
        }else {
            evaluatorIds = new ArrayList<>();
            evaluatedIds = new ArrayList<>();
        }


        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new DontFoundException(testId));

        // получение флага нужно ли выбирать кого оценивать
        boolean isGetRespondents = !(testEntity.getType() == TypeTestEnum.SELF);

        // формирование составного индекса и получение флага оценил ли пользователь сам себя по репорту
        String reportIndex = String.format("%d%s%d",userId, splitter, userId);

        boolean isCompleteEvaluation = reportRepository.findByEvaluatedIdTestIdEvaluatorId(reportIndex).isPresent();

        // TODO isActiveResult пока оставить true потом дописать логику
        boolean isActiveResult = true;

        // составление списков оценщиков и оцениваемых с моделью TestRespondentTitleModel
        // которая учитывает выполнил ли он тест
        List<TestRespondentTitleModel> evaluator = new ArrayList<>();
        List<TestRespondentTitleModel> evaluated = new ArrayList<>();

        // выбраны ли оцениваемые
        boolean isSelectRespondents;
        if (!evaluatorIds.isEmpty()) {
            evaluator = getEvaluatorRespondents(testId, userId, evaluatorIds);
        }

        if (!evaluatedIds.isEmpty()) {
            evaluated = getEvaluatedRespondents(testId, userId, evaluatedIds);
            isSelectRespondents = true;
        }else {
            isSelectRespondents = false;
        }

        // проверка все ли выполнили свои задачи
        boolean isCompeteEvaluator = isAllCompeted(evaluator);
        boolean isCompeteEvaluated = isAllCompeted(evaluated);


        return new TestMenuResponseDTO(testEntity.getTitle(), isGetRespondents,
                isSelectRespondents, isCompleteEvaluation,
                isCompeteEvaluated, isCompeteEvaluator,
                evaluated, evaluator, isActiveResult);
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
            QuestionEntity entity = testMapper.toQuestionEntity(entry.getValue());
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


    public TestEntity addTest(TestRequestDTO testRequestDTO) {
        return testRepository.save(testMapper.toTestEntity(testRequestDTO, generateQuestionsIds(testRequestDTO)));
    }

    public QuestionsResponseDTO getAllQuestions() {
        return new QuestionsResponseDTO(testMapper.toQuestionModelList(questionRepository.findAll()));
    }

    public TestEntity editTestStatus(String testID, TestStatusRequestDTO testStatusRequestDTO){
        TestEntity testEntity = testRepository.findById(testID).orElseThrow(() -> new DontFoundException(testID));
        testEntity.setStatus(testStatusRequestDTO.status());
        return testRepository.save(testEntity);
    }

    public SkillEntity addSkill(SkillRequestDTO skillRequestDto){
        return skillRepository.save(testMapper.toSkillEntity(skillRequestDto));
    }

    public List<SkillEntity> addSkills(List<SkillRequestDTO> skillRequestDTOList){
        return skillRepository.saveAll(testMapper.toSkillsEntity(skillRequestDTOList));
    }

    public List<SkillEntity> getSkills() {
        return skillRepository.findAll();
    }
}
