package ru.singularity.evaluation360.dto.result;

import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;

import java.util.List;


public record ResultApproveRequestDto (
    boolean approve,
    String comment,
    List<SkillsResultModel> results
){}
