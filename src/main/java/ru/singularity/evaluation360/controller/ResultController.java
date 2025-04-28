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

import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;

import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.ResultService;

import java.util.Arrays;


@Slf4j
@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;
    private final AuthService authService;

    /**
     * Получить результаты теста.
     *
     * @param test_id Идентификатор теста.
     * @return Результаты теста.
     */
    // TODO: add security (Access only for users from the test and only after being opened by an administrator)
    @Operation(summary = "Получить результаты теста", description = "Возвращает результаты прохождения теста для указанного test_id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение результатов теста"),
            @ApiResponse(responseCode = "404", description = "Результаты не найдены")
    })
    @GetMapping("/{test_id}")
    public ResponseEntity<ResultResponseDTO> getResult(
            @Parameter(description = "Идентификатор результата", required = true) @PathVariable String test_id) {

        int userId = authService.findUserByEmail(SecurityContextHolder.
                getContext().getAuthentication().getName()).getId();

        return ResponseEntity.ok(resultService.getResultByIndex(test_id, userId));
    }

    /**
     * Добавить результат.
     *
     * @param resultRequestDTO Данные о результате.
     * @return Статус HTTP 201 (Создано).
     */
    @Operation(summary = "Добавить результат", description = "Добавляет результат для прохождения теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Результат успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping("/{test_id}")
    @PreAuthorize("@testAuthFilter.hasTestAccess(#test_id, authentication.principal.id, authentication.principal.role)")
    public ResponseEntity<HttpStatus> addResult(@PathVariable String test_id,
                                                @RequestBody ResultRequestDTO resultRequestDTO) {
        try {
            resultService.addResult(test_id, resultRequestDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);

        }catch (Exception e) {
            log.error(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(o -> log.error(o.toString()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }
    }
}
