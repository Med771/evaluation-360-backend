package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import ru.singularity.evaluation360.dto.test.QuestionsResponseDTO;
import ru.singularity.evaluation360.dto.test.TestStatusRequestDTO;
import ru.singularity.evaluation360.dto.test.TestViewResponseDTO;
import ru.singularity.evaluation360.service.QuestionService;
import ru.singularity.evaluation360.service.TestManagementService;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final QuestionService questionService;
    private final TestManagementService testManagementService;

    /**
     *
     * Получить список вопросов
     */
    @Operation(summary = "получить все вопросы", description = "получить вопросы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "получение шаблонов вопросов")
    })
    @GetMapping("questions")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<QuestionsResponseDTO> getQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    /**
     * Получить информацию о тесте для админа.
     *
     * @param test_id Идентификатор теста.
     * @return Детали теста.
     */
    @Operation(summary = "Получить тест", description = "Возвращает тест.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение теста")
    })
    @GetMapping("test/{test_id}")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<TestViewResponseDTO> getTestAdmin(@PathVariable String test_id) {
        try {
            return ResponseEntity.ok(testManagementService.getTest(test_id));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    /**
     *
     * Изменение статуса.
     *
     * @param testStatusRequestDTO модель для изменения комментария
     */
    @PutMapping("status/{test_id}")
    @Operation(summary = "изменить статус", description = "меняет статус")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение теста")
    })
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<HttpStatus> updateTestStatus(@PathVariable String test_id, @RequestBody TestStatusRequestDTO testStatusRequestDTO) {
        try {
            testManagementService.editTestStatus(test_id, testStatusRequestDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
