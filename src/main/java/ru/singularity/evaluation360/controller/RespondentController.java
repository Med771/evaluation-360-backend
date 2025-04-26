package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ru.singularity.evaluation360.dto.respondent.RespondentsRequestDTO;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;

import ru.singularity.evaluation360.exeptions.DontFoundException;

import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.RespondentService;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/respondent")
@RequiredArgsConstructor
public class RespondentController {
    private final RespondentService respondentService;
    private final AuthService authService;

    /**
     * Получить список респондентов для указанного теста.
     *
     * @param test_id Идентификатор теста.
     * @return Ответ с данными респондентов для теста.
     */
    // TODO: add security (Access for test users only)
    @Operation(summary = "Получить респондентов для теста", description = "Возвращает список респондентов для указанного теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка респондентов"),
            @ApiResponse(responseCode = "404", description = "Тест не найден")
    })
    @GetMapping("/{test_id}")
    @PreAuthorize("@testAuthFilter.hasTestAccess(#test_id, authentication.principal.id, authentication.principal.role)")
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
    // TODO: add security (Access for test users only)
    @Operation(summary = "Выбрать респондентов", description = "Выбирает респондентов на основе предоставленных данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Респонденты успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping("/{test_id}")
    @PreAuthorize("@testAuthFilter.hasTestAccess(#test_id, authentication.principal.id, authentication.principal.role)")
    public ResponseEntity<HttpStatus> createRespondent(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable("test_id") String test_id,
            @RequestBody RespondentsRequestDTO respondentsRequestDTO) {
        Integer userId = authService.
                findUserByEmail(SecurityContextHolder.
                        getContext().
                        getAuthentication().
                        getName()).getId();


        try {
            respondentService.setRespondents(userId, test_id, respondentsRequestDTO);

            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e) {
            log.error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(o -> log.error(o.toString()));

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
