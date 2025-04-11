package ru.singularity.evaluation360.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.singularity.evaluation360.dto.result.CommentEditRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;

import java.util.List;

@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
public class ResultController {

    /**
     * Получить результаты теста.
     *
     * @param test_id Идентификатор теста.
     * @return Результаты теста.
     */
    @Operation(summary = "Получить результаты теста", description = "Возвращает результаты прохождения теста для указанного test_id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение результатов теста"),
            @ApiResponse(responseCode = "404", description = "Результаты не найдены")
    })
    @GetMapping("/{test_id}")
    public ResponseEntity<ResultResponseDTO> getResult(
            @Parameter(description = "Идентификатор теста", required = true) @PathVariable String test_id) {
        List<String> comment = List.of("1", "2", "3");
        List<SkillsResultModel> skillsResultModels = List.of(new SkillsResultModel("test", 2.2, 2.2,
                2.2, 2.2, comment));
        ResultResponseDTO resultResponseDTO = new ResultResponseDTO("String", 2.2, 2.2,
                2.2, 2.2, skillsResultModels, "dsfsdf");

        return ResponseEntity.ok(resultResponseDTO);
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
    @PostMapping()
    public ResponseEntity<HttpStatus> addResult(@RequestBody ResultRequestDTO resultRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(HttpStatus.CREATED);
    }


    /**
     * Изменить комментарий.
     *
     * @param skillIndex индекс скилла у которого мы меняем комментарий
     * @param commentIndex индекс комментария
     * @param commentEditRequestDTO измененный комментарий
     */
    @Operation(summary = "изменить комментарий", description = "меняет комментарий под нужным индексом")
    @PutMapping("/edit/comment/{skillIndex}/{commentIndex}")
    public ResponseEntity<HttpStatus> editComment(@PathVariable int skillIndex,
                                                  @PathVariable int commentIndex,
                                                  @RequestBody CommentEditRequestDTO commentEditRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(HttpStatus.OK);
    }
}
