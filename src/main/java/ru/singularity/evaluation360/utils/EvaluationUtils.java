package ru.singularity.evaluation360.utils;

import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.entity.ParticipantEntity;

import java.util.List;

public record EvaluationUtils(List<ParticipantEntity> evaluatesComplete, List<ParticipantEntity> evaluatesAll) {
    public static boolean isAllCompeted(List<TestRespondentTitleModel> testRespondentTitleModels) {
        if (testRespondentTitleModels.isEmpty()) {
            return false;
        }

        for (TestRespondentTitleModel testRespondentTitleModel : testRespondentTitleModels) {
            if (!testRespondentTitleModel.isComplete()) {
                return false;
            }
        }

        return true;
    }
}
