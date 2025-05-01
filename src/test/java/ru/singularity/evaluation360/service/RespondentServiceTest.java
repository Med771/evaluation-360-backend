package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import ru.singularity.evaluation360.dto.respondent.RespondentsRequestDTO;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.entity.EvaluationEntity;
import ru.singularity.evaluation360.entity.ParticipantEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.exeptions.RepeatException;
import ru.singularity.evaluation360.repository.EvaluationRepository;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RespondentServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private RespondentService respondentService;

    private final String splitter = "!_!*!_!";
    private final String testId = "test123";
    private final int userId = 1;
    private final String index = testId + splitter + userId;

    private TestEntity testEntity;
    private ParticipantEntity participantEntity;
    private UserEntity userEntity;
    private EvaluationEntity evaluationEntity;
    private RespondentsRequestDTO respondentsRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        respondentService = new RespondentService(
            splitter,
            testRepository,
            participantRepository,
            evaluationRepository
        );

        testEntity = new TestEntity();
        testEntity.setId(testId);
        testEntity.setTitle("Test Title");
        testEntity.setMinRespondents(2);
        testEntity.setMaxRespondents(5);
        testEntity.setMinHighRoleRespondents(1);
        testEntity.setParticipantsIds(Arrays.asList(1, 2, 3));

        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setRole(RoleUserEnum.USER);

        participantEntity = new ParticipantEntity();
        participantEntity.setId(1);
        participantEntity.setFullName("Test User");
        participantEntity.setCourse(1);
        participantEntity.setUser(userEntity);

        evaluationEntity = new EvaluationEntity();
        evaluationEntity.setIndex(index);
        evaluationEntity.setEvaluated(new ArrayList<>());
        evaluationEntity.setEvaluator(new ArrayList<>());

        respondentsRequestDTO = new RespondentsRequestDTO(Arrays.asList(2, 3));
    }

    @Test
    void setRespondents_Success() {
        when(evaluationRepository.findByIndex(index)).thenReturn(Optional.of(evaluationEntity));
        when(evaluationRepository.findAllByIndexIsStartingWith(testId)).thenReturn(Collections.singletonList(evaluationEntity));
        when(evaluationRepository.saveAll(any())).thenReturn(Collections.singletonList(evaluationEntity));

        respondentService.setRespondents(userId, testId, respondentsRequestDTO);

        verify(evaluationRepository).findByIndex(index);
        verify(evaluationRepository).findAllByIndexIsStartingWith(testId);
        verify(evaluationRepository).saveAll(any());
    }

    @Test
    void setRespondents_RepeatException() {
        evaluationEntity.setEvaluated(Arrays.asList(2, 3));
        when(evaluationRepository.findByIndex(index)).thenReturn(Optional.of(evaluationEntity));

        assertThrows(RepeatException.class, () -> respondentService.setRespondents(userId, testId, respondentsRequestDTO));
        verify(evaluationRepository).findByIndex(index);
        verify(evaluationRepository, never()).findAllByIndexIsStartingWith(any());
        verify(evaluationRepository, never()).saveAll(any());
    }

    @Test
    void getRespondents_Success() {
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(participantRepository.findAllById(testEntity.getParticipantsIds())).thenReturn(Collections.singletonList(participantEntity));

        RespondentsResponseDTO result = respondentService.getRespondents(testId);

        assertNotNull(result);
        assertEquals(testEntity.getMinRespondents(), result.minRespondents());
        assertEquals(testEntity.getMaxRespondents(), result.maxRespondents());
        assertEquals(testEntity.getMinHighRoleRespondents(), result.minHighRoleRespondents());
        assertEquals(1, result.respondents().size());
        verify(testRepository).findById(testId);
        verify(participantRepository).findAllById(testEntity.getParticipantsIds());
    }

    @Test
    void getRespondents_TestNotFound() {
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> respondentService.getRespondents(testId));
        verify(testRepository).findById(testId);
        verify(participantRepository, never()).findAllById(any());
    }
} 