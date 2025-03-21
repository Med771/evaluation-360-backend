package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.singularity.evaluation360.dto.test.TestMenuResponseDTO;
import ru.singularity.evaluation360.dto.test.TestResponseDTO;
import ru.singularity.evaluation360.dto.test.TestsResponseDTO;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;

import java.util.List;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestController {

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
    public ResponseEntity<TestsResponseDTO> getTests(){
        List<TestTitleModel> testTitleModel = List.of(new TestTitleModel(1L, "sdf", 234L, 2346L));

        TestsResponseDTO testsResponseDTO = new TestsResponseDTO("360", testTitleModel);

        return ResponseEntity.ok(testsResponseDTO);
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
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable Long test_id){
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
    @GetMapping("/{test_id}")
    public ResponseEntity<TestResponseDTO> getTest(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable Long test_id){
        List<QuestionTestModel> questionTestModel = List.of(new QuestionTestModel("quest", List.of(1, 2, 3)));
        TestResponseDTO testResponseDTO = new TestResponseDTO("fgd", 2L, 3L, questionTestModel);
        return ResponseEntity.ok(testResponseDTO);
    }
}
