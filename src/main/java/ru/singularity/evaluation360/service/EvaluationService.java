package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.TestMenuResponseDTO;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.ReportEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.BadRequestException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.TestRepository;
import ru.singularity.evaluation360.utils.EvaluationUtils;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final ParticipantRepository participantRepository;
    private final ReportRepository reportRepository;
    private final TestRepository testRepository;

    private final ParticipantsMapper participantsMapper;

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
        return generateTestRespondentModels(new EvaluationUtils(
                participantRepository.findAllById(evaluatedIds),
                participantRepository.findAllById(evaluated)));

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

        return generateTestRespondentModels(new EvaluationUtils(
                participantRepository.findAllById(evaluatorIds),
                participantRepository.findAllById(evaluator)));
    }

    /**
     * метод для нахождения кто оценил кого оценил а кто кого не оценил
     * @param evaluationUtils все кого должны оценить или те кто должен оценить
     * @return список TestRespondentTitleModel где указано выполнил ли он оценку
     */
    private List<TestRespondentTitleModel> generateTestRespondentModels(EvaluationUtils evaluationUtils) {

        // создания сета для дальнейшего нахождения всех кто не оценил
        Set<ParticipantEntity> incompleteParticipants = new HashSet<>(evaluationUtils.evaluatesAll());
        evaluationUtils.evaluatesComplete().forEach(incompleteParticipants::remove);

        // создания списка для добавления
        List<TestRespondentTitleModel> testRespondentTitleModels = new ArrayList<>();

        // те кто оценил
        for (ParticipantEntity participantEntity : evaluationUtils.evaluatesComplete()) {
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


        TestEntity testEntity = testRepository.findById(testId).orElseThrow(() -> new BadRequestException(testId));

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

        boolean isCompeteEvaluator = EvaluationUtils.isAllCompeted(evaluator);
        boolean isCompeteEvaluated = EvaluationUtils.isAllCompeted(evaluated);

        return new TestMenuResponseDTO(testEntity.getTitle(), isGetRespondents,
                isSelectRespondents, isCompleteEvaluation,
                isCompeteEvaluated, isCompeteEvaluator,
                evaluated, evaluator, isActiveResult);
    }
}
