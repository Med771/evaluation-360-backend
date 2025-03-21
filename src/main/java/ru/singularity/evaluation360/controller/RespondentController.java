package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.singularity.evaluation360.dto.respondent.RespondentsRequestDTO;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;

import java.util.List;

@RestController
@RequestMapping("/respondent")
@RequiredArgsConstructor
public class RespondentController {

    /**
     * Получить список респондентов для указанного теста.
     *
     * @param test_id Идентификатор теста.
     * @return Ответ с данными респондентов для теста.
     */
    @Operation(summary = "Получить респондентов для теста", description = "Возвращает список респондентов для указанного теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка респондентов"),
            @ApiResponse(responseCode = "404", description = "Тест не найден")
    })
    @GetMapping("/{test_id}")
    public ResponseEntity<RespondentsResponseDTO> respondent(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable("test_id") long test_id) {
        List<RespondentModel> respondentModel = List.of(new RespondentModel(1L, 1, "String", 1));
        RespondentsResponseDTO respondentsResponseDTO =
                new RespondentsResponseDTO(1, 7, 2, 2, respondentModel);

        return ResponseEntity.ok(respondentsResponseDTO);
    }

    /**
     * Создать респондентов.
     *
     * @param respondentsRequestDTO Данные о респондентах.
     * @return Статус HTTP 201 (Создано).
     */
    @Operation(summary = "Создать респондентов", description = "Создаёт новых респондентов на основе предоставленных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Респонденты успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping()
    public ResponseEntity<HttpStatus> createRespondent(@RequestBody RespondentsRequestDTO respondentsRequestDTO) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
