package ru.singularity.evaluation360.entity.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultModel {
    String skillText;

    Double self;
    List<Double> commandsValues = new ArrayList<>();
    List<String> commandsComments = new ArrayList<>();
    List<Double> expertsValues = new ArrayList<>();
    List<String> expertsComments = new ArrayList<>();
}
