package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;

import java.util.List;

@Document(collection = "results")
@Getter
@Setter
public class ResultEntity {
    @Id
    private String id;

    // Unique UserId!_!*!_!TestId
    @Indexed(unique = true)
    private String UserTestIndex;

    private String title;

    // Average Result
    private Double averageResult;

    // Results
    private Double thisResult;
    private Double commandResult;
    private Double expertResult;

    // Skills results
    private List<SkillsResultModel> results;

    // Final comment
    private String comment;
}
