package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "questions")
@Getter
@Setter
public class QuestionEntity {
    @Id
    private String id;

    @Transient
    private int originalIndex;

    private String questionText;
    private List<Integer> skillsIds = new ArrayList<>();
}
