package ru.singularity.evaluation360.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.singularity.evaluation360.dto.test.SkillRequestDTO;
import ru.singularity.evaluation360.entity.SkillEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SkillMapper {
    SkillEntity toSkillEntity(SkillRequestDTO skillResponseDto);
    List<SkillEntity> toSkillsEntity(List<SkillRequestDTO> skillRequestDTOS);
}
