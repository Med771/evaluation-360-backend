package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.entity.TestEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestMapper {

    @Mappings({@Mapping(target = "respondents", source = "respondents")})
    RespondentsResponseDTO toRespondentResponseDto(TestEntity testEntity,
                                                   List<RespondentModel> respondents);


}
