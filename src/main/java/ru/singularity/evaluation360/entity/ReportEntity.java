package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;

import java.util.List;

@Document
@Getter
@Setter
public class ReportEntity {
    // Id_
    @Id
    private String id;

    // id кого оценивают id теста id кто оценивает
    // разделитель !_!*!_!
    @Indexed(unique = true)
    private String evaluatedIdTestIdEvaluatorId;

    // Participant Id
    private Integer evaluatedId;
    private Integer evaluatorId;

    // Start and End date time
    private Long startTimeStamp;
    private Long endTimeStamp;

    // answers
    private List<AnswerTestModel> answers;

    // skills
    private List<SkillsTestModel> skills;
}
