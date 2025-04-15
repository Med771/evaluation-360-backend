package ru.singularity.evaluation360.dto.respondent.model;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;

/**
 * Модель респондента.
 */
@Schema(description = "Респондент")
public record RespondentModel(

        @Schema(description = "ID респондента", example = "123")
        Integer respondentId,

        @Schema(description = "Роль респондента", example = "USER")
        RoleUserEnum role,

        @Schema(description = "Полное имя респондента", example = "Иванов Иван Иванович")
        String fullName,

        @Schema(description = "Курс респондента", example = "3")
        Integer course
) {}
