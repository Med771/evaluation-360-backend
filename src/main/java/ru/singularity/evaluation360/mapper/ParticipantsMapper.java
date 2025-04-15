package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.entity.ParticipantEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParticipantsMapper {


    @Mappings({@Mapping(target = "respondentId", source = "id"),
                @Mapping(target = "role", source = "user.role")})
    RespondentModel toRespondentModel(ParticipantEntity participantEntity);

    List<RespondentModel> toRespondentModels(List<ParticipantEntity> participantEntities);

}
