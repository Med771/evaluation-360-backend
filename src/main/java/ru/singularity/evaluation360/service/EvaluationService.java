package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.TestMenuResponseDTO;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.log.annotation.LogEntryExit;
import ru.singularity.evaluation360.log.annotation.LogException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final ReportRepository reportRepository;
    private final TestRepository testRepository;


    private final String splitter;

    @LogEntryExit
    @LogException
    private boolean isAllCompleted(
            List<TestRespondentTitleModel> testRespondentTitleModels,
            List<String> indexes,
            Map<String, ReportEntity> reportMap,
            boolean isEvaluated) {

        boolean isAllCompleted = true;

        for (String reportIndex : indexes) {
            if (isAllCompleted && !reportMap.containsKey(reportIndex)) {
                isAllCompleted = false;
            }

            int userId = Integer.getInteger(reportIndex.split(splitter)[(isEvaluated) ? 0 : 2]);

            TestRespondentTitleModel testRespondentTitleModel = new TestRespondentTitleModel(
                    userId,
                    "",
                    reportMap.containsKey(reportIndex)
            );

            testRespondentTitleModels.add(testRespondentTitleModel);
        }

        return isAllCompleted;
    }

    /**
     * получение и формирование TestMenu
     *
     * @param testId id теста
     * @param userId id пользователя
     * @return TestMenuResponseDTO
     */
    @LogEntryExit
    @LogException
    public TestMenuResponseDTO getTestMenu(String testId, int userId) {
        String index = testId + splitter + userId;

        Optional<EvaluationEntity> evaluationEntity = evaluationRepository.findByIndex(index);
        TestEntity test = testRepository.findById(testId).orElseThrow(() -> new DontFoundException(testId));

        String selfReportIndex = userId + splitter + testId + splitter + userId;

        boolean isGetRespondents = !(test.getType() == TypeTestEnum.SELF);
        boolean isSelectRespondents = evaluationEntity.isPresent();
        boolean isCompleteEvaluation = reportRepository.existsByIndex(selfReportIndex);
        boolean isCompleteEvaluated = false;
        boolean isCompleteEvaluator = false;
        List<TestRespondentTitleModel> evaluated = new ArrayList<>();
        List<TestRespondentTitleModel> evaluator = new ArrayList<>();
        boolean isActiveResult = false; // TODO: change isResultApprove

        if (!isGetRespondents || !isSelectRespondents || !isCompleteEvaluation) {
            return new TestMenuResponseDTO(test.getTitle(),
                    isGetRespondents, isSelectRespondents,
                    isCompleteEvaluation, isCompleteEvaluated, isCompleteEvaluator,
                    evaluated, evaluator, isActiveResult);
        }

        EvaluationEntity evaluation = evaluationEntity.get();

        List<String> edReportIndexes = evaluation.getEvaluated().stream()
                .map(id -> id + splitter + testId + splitter + userId)
                .collect(Collectors.toList());

        List<String> orReportIndexes = evaluation.getEvaluator().stream()
                .map(id -> userId + splitter + testId + splitter + id)
                .collect(Collectors.toList());

        Map<String, ReportEntity> edReportMap = reportRepository.findByTestIdAndIndexIn(testId, edReportIndexes)
                .stream().collect(Collectors.toMap(ReportEntity::getIndex, reportEntity -> reportEntity));
        Map<String, ReportEntity> orReportMap = reportRepository.findByTestIdAndIndexIn(testId, orReportIndexes)
                .stream().collect(Collectors.toMap(ReportEntity::getIndex, reportEntity -> reportEntity));

        isCompleteEvaluated = isAllCompleted(evaluated, edReportIndexes, edReportMap, true);
        isCompleteEvaluator = isAllCompleted(evaluator, orReportIndexes, orReportMap, false);

        return new TestMenuResponseDTO(test.getTitle(),
                isGetRespondents, isSelectRespondents,
                isCompleteEvaluation, isCompleteEvaluated, isCompleteEvaluator,
                evaluated, evaluator, isActiveResult);
    }
}
