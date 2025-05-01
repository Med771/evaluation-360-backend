package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.singularity.evaluation360.dto.test.TestMenuResponseDTO;
import ru.singularity.evaluation360.dto.test.model.TestRespondentTitleModel;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.ReportEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private TestRepository testRepository;

    @Mock
    private ParticipantsMapper participantsMapper;

    @InjectMocks
    private EvaluationService evaluationService;

    private final String splitter = "!_!*!_!";
    private final String testId = "test123";
    private final int userId = 1;
    private final String index = testId + splitter + userId;
    private final String reportIndex = userId + splitter + testId + splitter +userId;

    private TestEntity testEntity;
    private EvaluationEntity evaluationEntity;
    private ParticipantEntity participantEntity;
    private ReportEntity reportEntity;
    private TestRespondentTitleModel testRespondentTitleModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        evaluationService = new EvaluationService(
                evaluationRepository,
                reportRepository,
                testRepository,
                splitter
        );

        testEntity = new TestEntity();
        testEntity.setId(testId);
        testEntity.setTitle("Test Title");
        testEntity.setType(TypeTestEnum.SELF);

        evaluationEntity = new EvaluationEntity();
        evaluationEntity.setIndex(index);
        evaluationEntity.setEvaluator(Arrays.asList(2, 3));
        evaluationEntity.setEvaluated(Arrays.asList(4, 5));

        participantEntity = new ParticipantEntity();
        participantEntity.setId(1);
        participantEntity.setFullName("Test User");

        reportEntity = new ReportEntity();
        reportEntity.setEvaluatedId(userId);
        reportEntity.setEvaluatorId(userId);
        reportEntity.setTestId(testId);

        testRespondentTitleModel = new TestRespondentTitleModel(
                1,
                "Test",
                true
        );
    }

    @Test
    void getTestMenu_Success() {
        when(evaluationRepository.findByIndex(index)).thenReturn(Optional.of(evaluationEntity));
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(reportRepository.existsByIndex(reportIndex)).thenReturn(true);
        when(participantRepository.findAllById(any())).thenReturn(Collections.singletonList(participantEntity));
        when(participantsMapper.toTestRespondentTitleModel(any(), anyBoolean())).thenReturn(testRespondentTitleModel);

        TestMenuResponseDTO result = evaluationService.getTestMenu(testId, userId);

        assertNotNull(result);
        assertEquals(testEntity.getTitle(), result.title());
        assertFalse(result.isGetRespondents());
        assertTrue(result.isSelectRespondents());
        assertTrue(result.isCompleteEvaluation());
        verify(evaluationRepository).findByIndex(index);
        verify(testRepository).findById(testId);
        verify(reportRepository).existsByIndex(reportIndex);
    }

    @Test
    void getTestMenu_TestNotFound() {
        when(evaluationRepository.findByIndex(index)).thenReturn(Optional.of(evaluationEntity));
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(DontFoundException.class, () -> evaluationService.getTestMenu(testId, userId));
        verify(evaluationRepository).findByIndex(index);
        verify(testRepository).findById(testId);
    }

    @Test
    void getTestMenu_NoEvaluation() {
        when(evaluationRepository.findByIndex(index)).thenReturn(Optional.empty());
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(reportRepository.findByIndex(reportIndex)).thenReturn(Optional.empty());
        when(participantRepository.findAllById(any())).thenReturn(Collections.singletonList(participantEntity));
        when(participantsMapper.toTestRespondentTitleModel(any(), anyBoolean())).thenReturn(testRespondentTitleModel);

        TestMenuResponseDTO result = evaluationService.getTestMenu(testId, userId);

        assertNotNull(result);
        assertEquals(testEntity.getTitle(), result.title());
        assertFalse(result.isGetRespondents());
        assertFalse(result.isSelectRespondents());
        assertFalse(result.isCompleteEvaluation());
        verify(evaluationRepository).findByIndex(index);
        verify(testRepository).findById(testId);
    }
} 