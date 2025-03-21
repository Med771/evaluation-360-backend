package ru.singularity.evaluation360.dto.respondent;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO для запроса списка респондентов, которые оценивают пользователя.
 */
@Schema(description = "Респонденты, которые оценивают пользователя")
public record RespondentsRequestDTO(

        @Schema(description = "Список ID респондентов", example = "[101, 102, 103]")
        List<Integer> respondentsIds
) {}
