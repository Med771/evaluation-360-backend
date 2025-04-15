package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class EvaluationEntity {
    @Id
    private String id;

    @Indexed(unique = true) // test_id!_!*!_!p_id
    private String index;

    private List<Integer> evaluated = new ArrayList<>();
    private List<Integer> evaluator = new ArrayList<>();
}
