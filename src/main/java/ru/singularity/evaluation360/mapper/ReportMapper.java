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
    ReportEntity toReportEntity(ResultRequestDTO resultRequestDTO, String testId, @Context String splitter);

    @AfterMapping
    default void setGeneratedIndex(@MappingTarget ReportEntity report,
                                   ResultRequestDTO dto,
                                   String testId,
                                   @Context String splitter) {
        report.setEvaluatedIdTestIdEvaluatorId(dto.evaluatedId() + splitter + testId + splitter + dto.evaluatorId());
    }

}
