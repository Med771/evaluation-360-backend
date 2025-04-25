package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.QuestionEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuestionMapper {
    QuestionModel toQuestionModel(QuestionEntity questionEntity);

    List<QuestionModel> toQuestionModelList(List<QuestionEntity> questionEntities);


    QuestionTestModel toQuestionTestModel(QuestionEntity questionEntity);

    QuestionEntity toQuestionEntity(QuestionTestModel questionTestModel);

    List<QuestionTestModel> toQuestionTestModelList(List<QuestionEntity> questionEntities);
}
