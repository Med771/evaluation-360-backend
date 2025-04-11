package ru.singularity.evaluation360.dto.test;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;

@Schema(description = "обновление статуса")
public record TestStatusRequestDTO(StatusTestEnum status) {
}
