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
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.service.RespondentService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/respondent")
@RequiredArgsConstructor
public class RespondentController {
    private final RespondentService respondentService;

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
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable("test_id") String test_id) {
        try {
            return ResponseEntity.ok(respondentService.getRespondents(test_id));
        }
        catch (DontFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Выбрать респондентов.
     *
     * @param respondentsRequestDTO Данные о респондентах.
     * @return Статус HTTP 201 (Создано).
     */
    @Operation(summary = "Выбрать респондентов", description = "Выбирает респондентов на основе предоставленных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Респонденты успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping("/{test_id}")
    public ResponseEntity<HttpStatus> createRespondent(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable("test_id") String test_id,
            @RequestBody RespondentsRequestDTO respondentsRequestDTO) {
        // TODO: get User id by Context Manager

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
