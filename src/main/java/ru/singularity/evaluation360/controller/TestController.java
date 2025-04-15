package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.service.TestService;

import java.util.List;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

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
        return ResponseEntity.ok(testService.getAllTests());
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
    public ResponseEntity<TestMenuResponseDTO> getTestMenu(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable String test_id) {
        List<TestRespondentTitleModel> testRespondentTitleModel =
                List.of(new TestRespondentTitleModel(1L, "String", true));

        TestMenuResponseDTO testMenuResponseDTO = new TestMenuResponseDTO("String", true,
                false, true, true, false,
                testRespondentTitleModel, testRespondentTitleModel, true);

        return ResponseEntity.ok(testMenuResponseDTO);
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
    public ResponseEntity<TestResponseDTO> getTest(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable String test_id,
            @PathVariable long evaluatedId) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //TODO получить id пользователя

        return ResponseEntity.ok(testService.getTest(test_id, 1L, evaluatedId));
    }

    /**
     * получить информацию о тесте для админа.
     *
     * @param test_id Идентификатор теста.
     * @return Детали теста.
     */
    @Operation(summary = "Получить тест", description = "Возвращает тест.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение теста")
    })
    @GetMapping("admin/{test_id}")
    public ResponseEntity<TestViewResponseDTO> getTestAdmin(@PathVariable String test_id) {
        return ResponseEntity.ok(testService.getTest(test_id));
    }

    /**
     *
     * изменение статуса.
     *
     * @param testStatusRequestDTO модель для изменения комментария
     */
    @PutMapping("status/{test_id}")
    @Operation(summary = "изменить статус", description = "меняет статус")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение теста")
    })
    public ResponseEntity<HttpStatus> updateTestStatus(@PathVariable String test_id,@RequestBody TestStatusRequestDTO testStatusRequestDTO){
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     *
     * добовление теста
     * @param testRequestDTO тест
     */
    @PostMapping()
    @Operation(summary = "добавить тест", description = "добовляет тест")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное добовление теста")
    })
    public ResponseEntity<HttpStatus> postTest(@RequestBody TestRequestDTO testRequestDTO){
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    /**
     *
     * получить список вопросов
     */

    @Operation(summary = "получить все вопросы", description = "получить вопросы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "получение шаблонов вопросов")
    })
    @GetMapping("questions")
    public ResponseEntity<QuestionsResponseDTO> getQuestions() {
        return ResponseEntity.ok(new QuestionsResponseDTO(null));
    }
}
