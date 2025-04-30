package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.service.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {
    private final CustomUserDetailsService userDetails;

    private final TestManagementService testManagementService;
    private final EvaluationService evaluationService;

    /**
     * Получить все тесты.
     *
     * @return Список всех тестов.
     */
    @Operation(summary = "Получить все тесты", description = "Возвращает все доступные тесты в системе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка тестов")
    })
    @GetMapping()
    public ResponseEntity<TestsResponseDTO> getTests() {
        Integer userId = ((UserEntity) userDetails.
                loadUserByUsername(SecurityContextHolder.
                        getContext().
                        getAuthentication().
                        getName())).getId();

        return ResponseEntity.ok(testManagementService.getAllTests(userId));
    }

    /**
     * Получить меню теста.
     *
     * @param test_id Идентификатор теста.
     * @return Меню для указанного теста.
     */
    @Operation(summary = "Получить меню теста", description = "Возвращает меню для указанного теста.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение меню теста")
    })
    @GetMapping("menu/{test_id}")
    @PreAuthorize("@testAuthFilter.hasTestAccess(#test_id, authentication.principal.id, authentication.principal.role)")
    public ResponseEntity<TestMenuResponseDTO> getTestMenu(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable String test_id) {
        Integer userId = ((UserEntity) userDetails.
                loadUserByUsername(SecurityContextHolder.
                        getContext().
                        getAuthentication().
                        getName())).getId();

        return ResponseEntity.ok(evaluationService.getTestMenu(test_id, userId));
    }

    /**
     * Получить тест по его идентификатору.
     *
     * @param test_id Идентификатор теста.
     * @return Детали теста.
     */
    @Operation(summary = "Получить тест", description = "Возвращает тест с вопросами и респондентами по заданному test_id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение теста")
    })
    @GetMapping("/{test_id}/{evaluatedId}")
    @PreAuthorize("@testAuthFilter.hasTestAccess(#test_id, authentication.principal.id, authentication.principal.role)")
    public ResponseEntity<TestResponseDTO> getTest(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable String test_id,
            @PathVariable long evaluatedId) {
        Integer userId = ((UserEntity) userDetails.
                loadUserByUsername(SecurityContextHolder.
                        getContext().
                        getAuthentication().
                        getName())).getId();

        return ResponseEntity.ok(testManagementService.getTest(test_id, userId, evaluatedId));
    }

    /**
     *
     * Добавление теста
     * @param testRequestDTO тест
     */
    @PostMapping()
    @Operation(summary = "добавить тест", description = "добавляет тест")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешное добавление теста")
    })
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<HttpStatus> postTest(@RequestBody TestRequestDTO testRequestDTO){
        try {
            testManagementService.addTest(testRequestDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        }catch (Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }
    }
}
