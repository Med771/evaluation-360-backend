package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.ResultModel;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.repository.*;

import java.util.*;
import java.util.stream.Collectors;

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
    public void checkTests() {

    }

    private void calculateResults(TestEntity test) {
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

            UserEntity user = users.get(value.getEvaluatorId());
            List<SkillsTestModel> reportSkills = value.getSkills();

            for (SkillsTestModel skill: reportSkills) {
                if (!results.containsKey(value.getEvaluatorId()) || !skills.containsKey(skill.skillId())) {
                    results.put(value.getEvaluatorId(), Map.of(skill.skillId(), new ResultModel()));
                }

                ResultModel result = results.get(value.getEvaluatorId()).get(skill.skillId());

                result.setSkillText(skills.get(skill.skillId()).getSkillsText());

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

            for (Map.Entry<Integer, ResultModel> resultEntry: entry.getValue().entrySet()) {
                double selfValue = resultEntry.getValue().getSelf();
                double commandValue = resultEntry.getValue().getCommandsValues().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0);
                double expertValue = resultEntry.getValue().getExpertsValues().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0);
                double averageValue = (selfValue + commandValue + expertValue) / 3;

                        SkillsResultModel skillsResultModel = new SkillsResultModel(
                        resultEntry.getValue().getSkillText(),
                        averageValue,
                        selfValue,
                        commandValue,
                        expertValue,
                        new ArrayList<>());
            }

            resultsEntities.add(resultEntity);
        }
    }
}
