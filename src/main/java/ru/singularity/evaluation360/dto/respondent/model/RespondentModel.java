package ru.singularity.evaluation360.dto.respondent.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Модель респондента.
 */
@Schema(description = "Респондент")
public record RespondentModel(

        @Schema(description = "ID респондента", example = "123")
        Long respondentId,

        @Schema(description = "ID роли респондента", example = "2")
        Integer roleId,

        @Schema(description = "Полное имя респондента", example = "Иванов Иван Иванович")
        String fullName,

        @Schema(description = "Курс респондента", example = "3")
        Integer course
) {}
