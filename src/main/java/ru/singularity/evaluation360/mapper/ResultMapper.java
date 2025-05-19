package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;
import ru.singularity.evaluation360.entity.ResultEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResultMapper {

    ResultResponseDTO toResultResponseDTO(ResultEntity result);
}
