package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
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

    @Scheduled(fixedRate = 60000)
    public void checkTests() {

    }

    private void calculateResults(TestEntity test) {
        List<ReportEntity> reports = reportRepository.findAllByTestId(test.getId());

        Map<Integer, UserEntity> users = userRepository.findAll()
                .stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

        HashMap<Integer, Map<Integer, ResultModel>> results = new HashMap<>();

        for (ReportEntity value: reports) {
            if (!users.containsKey(value.getEvaluatorId())) {
                continue;
            }

            UserEntity user = users.get(value.getEvaluatorId());
            List<SkillsTestModel> skills = value.getSkills();

            for (SkillsTestModel skill: skills) {
                if (!results.containsKey(value.getEvaluatorId())) {
                    results.put(value.getEvaluatorId(), Map.of(skill.skillId(), new ResultModel()));
                }

                ResultModel result = results.get(value.getEvaluatorId()).get(skill.skillId());

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
    }
}
