package ru.singularity.evaluation360.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.ReportEntity;
import ru.singularity.evaluation360.entity.ResultEntity;
import ru.singularity.evaluation360.exeptions.RepeatException;
import ru.singularity.evaluation360.mapper.ReportMapper;
import ru.singularity.evaluation360.mapper.ResultMapper;
import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.ResultRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private ReportRepository reportRepository;

    private final String splitter = "!_!*!_!";

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private ResultMapper resultMapper;

    @InjectMocks
    private ResultService resultService;

    private ResultEntity resultEntity;

    private ResultResponseDTO resultResponseDTO;

    private ResultRequestDTO resultRequestDTO;

    private final String testId = "test123";
    private final int userId = 1;

    private final String index = testId + splitter + userId;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resultService = new ResultService(
                resultRepository,
                reportRepository,
                reportMapper,
                resultMapper,
                splitter
        );

        resultEntity = new ResultEntity();
        resultEntity.setResults(new ArrayList<>());
        resultEntity.setCommandResult(2.0);
        resultEntity.setThisResult(2.0);
        resultEntity.setExpertResult(2.0);
        resultEntity.setAverageResult(2.0);
        resultEntity.setComment("test");
        resultEntity.setTitle("test");
        resultEntity.setUserTestIndex(index);

        resultResponseDTO = new ResultResponseDTO("test", 2.0, 2.0,
                2.0, 2.0, new ArrayList<>(), "test");

        resultRequestDTO = new ResultRequestDTO(1,
                2,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                Arrays.asList(new AnswerTestModel(3, "Good answer"),
                        new AnswerTestModel(4, "Excellent answer")), Arrays.asList(
                new SkillsTestModel(1, 3.5), new SkillsTestModel(2, 4.0)));


    }


    @Test
    void getResultByIndex_Success() {
        when(resultRepository.findByUserTestIndex(anyString())).thenReturn(Optional.of(resultEntity));
        when(resultMapper.toResultResponseDTO(resultEntity)).thenReturn(resultResponseDTO);

        ResultResponseDTO result = resultService.getResultByIndex(testId, userId);

        assertEquals(resultResponseDTO, result);
        verify(resultRepository).findByUserTestIndex(index);
        verify(resultMapper).toResultResponseDTO(resultEntity);
    }

    @Test
    void getResultByIndex_NotFound() {
        when(resultRepository.findByUserTestIndex(index)).thenReturn(Optional.empty());

        assertThrows(DontFoundException.class, () -> resultService.getResultByIndex(testId, userId));
        verify(resultRepository).findByUserTestIndex(index);
        verify(resultMapper, never()).toResultResponseDTO(any());
    }

    @Test
    void addResult_Success() {

        String testId = "test123";

        String reportIndex = resultRequestDTO.evaluatedId() + splitter + testId + splitter + resultRequestDTO.evaluatorId();
        ReportEntity reportEntity = new ReportEntity();

        when(reportRepository.findByEvaluatedIdTestIdEvaluatorId(reportIndex)).thenReturn(Optional.empty());
        when(reportMapper.toReportEntity(resultRequestDTO, testId, splitter)).thenReturn(reportEntity);
        when(reportRepository.save(reportEntity)).thenReturn(reportEntity);

        resultService.addResult(testId, resultRequestDTO);

        verify(reportRepository).findByEvaluatedIdTestIdEvaluatorId(reportIndex);
        verify(reportMapper).toReportEntity(resultRequestDTO, testId, splitter);
        verify(reportRepository).save(reportEntity);
    }

    @Test
    void addResult_RepeatException() {
        String reportIndex = resultRequestDTO.evaluatedId() + splitter + testId + splitter + resultRequestDTO.evaluatorId();

        when(reportRepository.findByEvaluatedIdTestIdEvaluatorId(reportIndex)).thenReturn(Optional.of(new ReportEntity()));

        assertThrows(RepeatException.class, () -> resultService.addResult(testId, resultRequestDTO));
        verify(reportRepository).findByEvaluatedIdTestIdEvaluatorId(reportIndex);
        verify(reportMapper, never()).toReportEntity(any(), any(), any());
        verify(reportRepository, never()).save(any());
    }
} 