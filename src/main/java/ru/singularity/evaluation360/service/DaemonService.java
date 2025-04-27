package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.ResultModel;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.repository.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final Double conversionRate;

    @Scheduled(fixedRateString = "${daemon.fixedRate:60000}")
    public void checkTests() {
        log.info("DaemonService.checkTests()");
        long now = System.currentTimeMillis();

        List<TestEntity> toStart = testRepository
                .findByStatusAndStartTimeStampLessThanEqual(StatusTestEnum.CREATED, now);
        List<TestEntity> toArchive = testRepository
                .findByStatusAndEndTimeStampLessThanEqual(StatusTestEnum.STARTED, now);

        toStart.forEach(t -> t.setStatus(StatusTestEnum.STARTED));
        toArchive.forEach(t -> {
            t.setStatus(StatusTestEnum.ARCHIVED);
            calculateResults(t);
        });

        List<TestEntity> all = Stream.concat(toStart.stream(), toArchive.stream())
                .collect(Collectors.toList());

        if (!all.isEmpty()) {
            testRepository.saveAll(all);
            log.info("Updated {} tests", all.size());
        }
    }

    private void setResult(
            HashMap<Integer, Map<Integer, ResultModel>> results,
            Map<Integer, SkillEntity> skills,
            ReportEntity value,
            RoleUserEnum role) {

        for (SkillsTestModel skill : value.getSkills()) {
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
                result.setSelf(skill.value() * conversionRate);
            } else if (role == RoleUserEnum.USER) {
                result.getCommandsValues().add(skill.value() * conversionRate);
            } else {
                result.getExpertsValues().add(skill.value() * conversionRate);
            }

        }
    }

    private HashMap<Integer, Map<Integer, ResultModel>> getResults(List<ReportEntity> reports, Map<Integer, UserEntity> users) {
        Map<Integer, SkillEntity> skills = skillRepository.findAll()
                .stream().collect(Collectors.toMap(SkillEntity::getId, skill -> skill));

        HashMap<Integer, Map<Integer, ResultModel>> results = new HashMap<>();

        for (ReportEntity value : reports) {
            if (!users.containsKey(value.getEvaluatorId())) {
                continue;
            }

            UserEntity user = users.get(value.getEvaluatedId());

            setResult(results, skills, value, user.getRole());
        }

        return results;
    }

    private double average(List<Double> values) {
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private ResultEntity setResultEntity(Map.Entry<Integer, Map<Integer, ResultModel>> entry, TestEntity test) {
        ResultEntity resultEntity = new ResultEntity();

        resultEntity.setUserTestIndex(entry.getKey() + splitter + test.getId());
        resultEntity.setTitle(test.getTitle());
        resultEntity.setComment("Final comment");
        resultEntity.setResults(new ArrayList<>());

        List<Double> averageResult = new ArrayList<>();
        List<Double> selfResult = new ArrayList<>();
        List<Double> commandsResult = new ArrayList<>();
        List<Double> expertsResult = new ArrayList<>();

        for (Map.Entry<Integer, ResultModel> resultEntry : entry.getValue().entrySet()) {
            double selfValue = resultEntry.getValue().getSelf();
            double commandValue = average(resultEntry.getValue().getCommandsValues());
            double expertValue = average(resultEntry.getValue().getExpertsValues());
            double averageValue;

            if (test.getType() == TypeTestEnum.SELF) {
                averageValue = selfValue;
            } else if (test.getType() == TypeTestEnum.COMMAND) {
                averageValue = selfValue * 0.2 + commandValue * 0.8;
            } else {
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

        resultEntity.setAverageResult(average(averageResult));
        resultEntity.setThisResult(average(selfResult));
        resultEntity.setCommandResult(average(commandsResult));
        resultEntity.setExpertResult(average(expertsResult));

        return resultEntity;
    }

    @Async
    protected void calculateResults(TestEntity test) {
        List<ReportEntity> reports = reportRepository.findAllByTestId(test.getId());

        Map<Integer, UserEntity> users = userRepository.findAll()
                .stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

        HashMap<Integer, Map<Integer, ResultModel>> results = getResults(reports, users);

        List<ResultEntity> resultsEntities = results.entrySet()
                .stream().map(entry -> setResultEntity(entry, test)).collect(Collectors.toList());

        resultRepository.saveAll(resultsEntities);
    }
}
