package ru.singularity.evaluation360.dto.result;

import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

import java.util.List;

@ValidateFieldsNotNullOrBlank
public record ResultApproveRequestDto (
    boolean approve,
    String comment,
    List<SkillsResultModel> results
){}
