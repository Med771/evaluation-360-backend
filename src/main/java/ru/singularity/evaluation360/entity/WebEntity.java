package ru.singularity.evaluation360.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document(collection = "webs")
@Setter
@Getter
public class WebEntity {
    @Id
    private String id;

    private HashMap<String, Integer> studentWeb;
}
