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
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.repository.UserRepository;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.TestService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final AuthService authService;

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
        int userId = authService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .getId();

        return ResponseEntity.ok(testService.getAllTests(userId));
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
        int userId = authService.
                findUserByEmail(SecurityContextHolder.
                        getContext().
                        getAuthentication().
                        getName()).getId();
        return ResponseEntity.ok(testService.getTestMenu(test_id, userId));
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
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = authService.findUserByEmail(name).getId();

        return ResponseEntity.ok(testService.getTest(test_id, userId, evaluatedId));
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
    @GetMapping("admin/{test_id}")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<TestViewResponseDTO> getTestAdmin(@PathVariable String test_id) {
        return ResponseEntity.ok(testService.getTest(test_id));
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
    public ResponseEntity<HttpStatus> updateTestStatus(@PathVariable String test_id,@RequestBody TestStatusRequestDTO testStatusRequestDTO){
        try {
            testService.editTestStatus(test_id, testStatusRequestDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        }catch (Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }
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
            testService.addTest(testRequestDTO);
            return ResponseEntity.ok(HttpStatus.CREATED);
        }catch (Exception e){
            log.error(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }
    }

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
        return ResponseEntity.ok(testService.getAllQuestions());
    }

    /**
     * Добавление навыка
     */
    @PostMapping("skill")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<SkillEntity> addSkill(@RequestBody SkillRequestDTO skillRequestDTO){
        return ResponseEntity.ok(testService.addSkill(skillRequestDTO));
    }

    /**
     * Добавление навыков
     */
    @PostMapping("skills")
    public ResponseEntity<List<SkillEntity>> addSkills(@RequestBody List<SkillRequestDTO> skillRequestDTOS){
        return ResponseEntity.ok(testService.addSkills(skillRequestDTOS));
    }
    /**
     * Получить навыки
     */
    @GetMapping("skills")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<List<SkillEntity>> getSkills() {
        return ResponseEntity.ok(testService.getSkills());
    }
}
