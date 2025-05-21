package ru.singularity.evaluation360.dto.test;

import ru.singularity.evaluation360.validator.anotation.ValidateFieldsNotNullOrBlank;

@ValidateFieldsNotNullOrBlank
public record SkillRequestDTO(String skillsText){}
