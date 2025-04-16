package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.test.TestRequestDTO;
import ru.singularity.evaluation360.dto.test.TestResponseDTO;
import ru.singularity.evaluation360.dto.test.TestViewResponseDTO;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.entity.TestEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestMapper {

    /**
     *
     * @param testEntity модель теста
     * @param respondents список респондентов которые были добавленные в тест
     */
    @Mappings({@Mapping(target = "respondents", source = "respondents")})
    RespondentsResponseDTO toRespondentResponseDto(TestEntity testEntity,
                                                   List<RespondentModel> respondents);

    QuestionModel toQuestionModel(QuestionEntity questionEntity);

    List<QuestionModel> toQuestionModelList(List<QuestionEntity> questionEntities);


    QuestionTestModel toQuestionTestModel(QuestionEntity questionEntity);

    QuestionEntity toQuestionEntity(QuestionTestModel questionTestModel);

    List<QuestionTestModel> toQuestionTestModelList(List<QuestionEntity> questionEntities);

    /**
     *
     * @param testEntity модель теста
     * @param respondentModels список респондентов которые были добавленные в тест
     * @param questionModels список вопроса теста
     */
    @Mappings ({
            @Mapping(target = "respondents", source = "respondentModels"),
            @Mapping(target = "questions", source = "questionModels")
    })
    TestViewResponseDTO toTestViewResponseDTO(TestEntity testEntity,
                                              List<RespondentModel> respondentModels,
                                              List<QuestionModel> questionModels);

    List<TestViewResponseDTO> toTestViewResponseDtoList(List<TestEntity> testEntities);

    /**
     *
     * @param testRequestDTO модель для создания теста
     * @param questionsIds id вопросов
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "questionsIds", source = "questionsIds"),
    })
    TestEntity toTestEntity(TestRequestDTO testRequestDTO,
                            List<String> questionsIds);

    @Mappings({
            @Mapping(target = "testId", source = "testEntity.id")
    })
    TestTitleModel toTitleModel(TestEntity testEntity);

    List<TestTitleModel> toTitleModelList(List<TestEntity> testEntities);
}
