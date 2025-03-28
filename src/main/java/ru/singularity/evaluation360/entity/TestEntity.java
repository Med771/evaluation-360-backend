package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;

import java.util.List;

@Document(collection = "tests")
@Getter
@Setter
public class TestEntity {
    @Id
    private String id;

    // Title test
    private String title;

    // Type
    private TypeTestEnum type;

    // Status
    private StatusTestEnum status;

    // Date time
    private Long createTimeStamp;
    private Long startTimeStamp;
    private Long endTimeStamp;

    // Participants
    private List<Integer> participantsIds;

    // Questions
    private List<String> questionsIds;

    // Min/Max Respondents
    private Integer minRespondents;
    private Integer maxRespondents;
    private Integer minHighRoleRespondents;
    private Integer minOtherCourseRespondents;
}
