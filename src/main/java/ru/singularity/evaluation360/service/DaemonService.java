package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.ResultModel;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.log.annotation.LogEntryExit;
import ru.singularity.evaluation360.log.annotation.LogException;
import ru.singularity.evaluation360.log.annotation.LogMethod;
import ru.singularity.evaluation360.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DaemonService {
    private final ReportRepository reportRepository;
    private final ResultRepository resultRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    private final String splitter;

    @Scheduled(fixedRate = 60000)
    @LogEntryExit
    @LogException
    @LogMethod
    public void checkTests() {
        log.info("Checking tests...");
        List<TestEntity> testEntities = testRepository.findAll();
        long currentTimeInSeconds = System.currentTimeMillis();
        List<TestEntity> updEntities = new ArrayList<>();

        for (TestEntity testEntity : testEntities) {
            boolean isUpdated = false;

            if (testEntity.getStatus() == StatusTestEnum.CREATED &&
                    testEntity.getStartTimeStamp() <= currentTimeInSeconds) {
                log.info("edit status to started");
                testEntity.setStatus(StatusTestEnum.STARTED);
                isUpdated = true;
            }

            if (testEntity.getStatus() == StatusTestEnum.STARTED &&
                    testEntity.getEndTimeStamp() <= currentTimeInSeconds) {
                calculateResults(testEntity);
                log.info("edit status to archived");
                testEntity.setStatus(StatusTestEnum.ARCHIVED);
                isUpdated = true;
            }

            if (isUpdated) {
                log.info("update status");
                updEntities.add(testEntity);
            }
        }

        testRepository.saveAll(updEntities);
    }


    @Async
    @LogEntryExit
    @LogException
    protected void calculateResults(TestEntity test) {
        List<ReportEntity> reports = reportRepository.findAllByTestId(test.getId());

        Map<Integer, SkillEntity> skills = skillRepository.findAll()
                .stream().collect(Collectors.toMap(SkillEntity::getId, skill -> skill));
        Map<Integer, UserEntity> users = userRepository.findAll()
                .stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

        HashMap<Integer, Map<Integer, ResultModel>> results = new HashMap<>();

        for (ReportEntity value: reports) {
            if (!users.containsKey(value.getEvaluatorId())) {
                continue;
            }

            UserEntity user = users.get(value.getEvaluatedId());
            List<SkillsTestModel> reportSkills = value.getSkills();

            for (SkillsTestModel skill: reportSkills) {
                if (!results.containsKey(value.getEvaluatorId())) {
                    results.put(value.getEvaluatorId(), new HashMap<>());
                }

                if (!results.get(value.getEvaluatorId()).containsKey(skill.skillId())) {
                    results.get(value.getEvaluatorId()).put(skill.skillId(), new ResultModel());
                }

                ResultModel result = results.get(value.getEvaluatorId()).get(skill.skillId());

                result.setSkillText(skills.get(skill.skillId()).getSkillsText());
                log.info(result.toString());

                if (Objects.equals(value.getEvaluatedId(), value.getEvaluatorId())) {
                    result.setSelf(skill.value() / 4 * 10);
                }
                if (user.getRole() == RoleUserEnum.USER) {
                    result.getCommandsValues().add(skill.value() / 4 * 10);
                }
                else {
                    result.getExpertsValues().add(skill.value() / 4 * 10);
                }

            }
        }

        List<ResultEntity> resultsEntities = new ArrayList<>();

        for (Map.Entry<Integer, Map<Integer, ResultModel>> entry: results.entrySet()) {
            ResultEntity resultEntity = new ResultEntity();

            resultEntity.setUserTestIndex(entry.getKey() + splitter + test.getId());
            resultEntity.setTitle(test.getTitle());
            resultEntity.setComment("Final comment");
            resultEntity.setResults(new ArrayList<>());

            List<Double> averageResult = new ArrayList<>();
            List<Double> selfResult = new ArrayList<>();
            List<Double> commandsResult = new ArrayList<>();
            List<Double> expertsResult = new ArrayList<>();

            for (Map.Entry<Integer, ResultModel> resultEntry: entry.getValue().entrySet()) {
                double selfValue = resultEntry.getValue().getSelf();
                double commandValue = resultEntry.getValue().getCommandsValues().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0);
                double expertValue = resultEntry.getValue().getExpertsValues().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0);
                double averageValue;

                if (test.getType() == TypeTestEnum.SELF) {
                    averageValue = selfValue;
                }
                else if (test.getType() == TypeTestEnum.COMMAND) {
                    averageValue = selfValue * 0.2 + commandValue * 0.8;
                }
                else {
                    averageValue = selfValue * 0.2 + commandValue * 0.3 + expertValue * 0.5;
                }

                selfResult.add(selfValue);
                commandsResult.add(commandValue);
                expertsResult.add(expertValue);
                averageResult.add(averageValue);

                SkillsResultModel skillsResultModel = new SkillsResultModel(
                        resultEntry.getValue().getSkillText(),
                        averageValue,
                        selfValue,
                        commandValue,
                        expertValue,
                        new ArrayList<>());

                resultEntity.getResults().add(skillsResultModel);
            }


            resultEntity.setAverageResult(averageResult.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            resultEntity.setThisResult(selfResult.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            resultEntity.setCommandResult(commandsResult.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            resultEntity.setExpertResult(expertsResult.stream().mapToDouble(Double::doubleValue).average().orElse(0));

            resultsEntities.add(resultEntity);
        }

        resultRepository.saveAll(resultsEntities);
    }
}
