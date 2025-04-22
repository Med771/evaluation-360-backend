package ru.singularity.evaluation360.mapper;

import org.mapstruct.*;
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

    @AfterMapping
    default void setGeneratedIndex(@MappingTarget ReportEntity report,
                                   ResultRequestDTO dto,
                                   String testId) {
        report.setId(dto.evaluatedId() + testId + dto.evaluatorId());
    }

}
