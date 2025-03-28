package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;

import java.util.Map;

public record TestRequestDTO(
        String title,
        TypeTestEnum type,
        Long createTimeStamp,
        Long startTimeStamp,
        Long endTimeStamp,

        Map<Integer, String> questionIds,
        Map<Integer, QuestionTestModel> newQuestions,

        Integer minRespondents,
        Integer maxRespondents,
        Integer minHighRoleRespondents,
        Integer minOtherCourseRespondents) {
}
