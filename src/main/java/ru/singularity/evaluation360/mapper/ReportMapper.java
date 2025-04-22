package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.entity.ReportEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportMapper {

    // TODO 1 из 2 моделей
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "testId", source = "testId")
    })
    ReportEntity toReportEntity(ResultRequestDTO resultRequestDTO, String testId);

}
