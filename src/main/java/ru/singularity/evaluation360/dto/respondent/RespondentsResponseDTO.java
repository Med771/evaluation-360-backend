package ru.singularity.evaluation360.dto.respondent;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;

import java.util.List;

/**
 * DTO с информацией о странице выбора респондентов.
 */
@Schema(description = "Информация о выборе респондентов")
public record RespondentsResponseDTO(

        @Schema(description = "Минимальное количество респондентов", example = "3")
        Integer minRespondents,

        @Schema(description = "Максимальное количество респондентов", example = "10")
        Integer maxRespondents,

        @Schema(description = "Минимальное количество администраторов, экспертов или кураторов", example = "1")
        Integer minHighRoleRespondents,

        @Schema(description = "Минимальное количество респондентов с другого курса", example = "2")
        Integer minOtherCourseRespondents,

        @Schema(description = "Список респондентов")
        List<RespondentModel> respondents
) {}
