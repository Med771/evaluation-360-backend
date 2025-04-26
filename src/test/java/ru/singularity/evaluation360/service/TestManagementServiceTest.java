package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.DontFoundException;
import ru.singularity.evaluation360.mapper.ParticipantsMapper;
import ru.singularity.evaluation360.mapper.QuestionMapper;
import ru.singularity.evaluation360.mapper.SkillMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.ParticipantRepository;
import ru.singularity.evaluation360.repository.QuestionRepository;
import ru.singularity.evaluation360.repository.SkillRepository;
import ru.singularity.evaluation360.repository.TestRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestManagementServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private TestMapper testMapper;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private ParticipantsMapper participantsMapper;

    @InjectMocks
    private TestManagementService testManagementService;

    private final String testId = "test123";
    private final int userId = 1;
    private final int evaluatedId = 2;

    private TestEntity testEntity;
    private QuestionEntity questionEntity;
    private QuestionTestModel questionTestModel;
    private TestTitleModel testTitleModel;
    private TestRequestDTO testRequestDTO;
    private TestStatusRequestDTO testStatusRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testManagementService = new TestManagementService(
                testRepository,
                questionRepository,
                participantRepository,
                skillRepository,
                testMapper,
                questionMapper,
                skillMapper,
                participantsMapper
        );

        testEntity = new TestEntity();
        testEntity.setId(testId);
        testEntity.setTitle("Test Title");
        testEntity.setStatus(StatusTestEnum.CREATED);
        testEntity.setQuestionsIds(Arrays.asList("q1", "q2"));
        testEntity.setParticipantsIds(Arrays.asList(1, 2));

        questionEntity = new QuestionEntity();
        questionEntity.setId("test");
        questionEntity.setOriginalIndex(1);
        questionEntity.setQuestionText("Question 1");

        questionTestModel = new QuestionTestModel("test", List.of(1));

        testTitleModel = new TestTitleModel(testId, "Test Title", 1L, 2L);

        testRequestDTO = new TestRequestDTO(
                "Test Title",
                TypeTestEnum.FULL,
                5L,
                1L,
                3L,
                Map.of(1, "string"),
                Map.of(2, questionTestModel),
                List.of(1, 2, 3),
                1,
                1,
                1,
                1,
                List.of(new SkillRequestDTO("skill"))
        );

        testStatusRequestDTO = new TestStatusRequestDTO(StatusTestEnum.CREATED);
    }

    @Test
    void editTestStatus_Success() {
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(testRepository.save(any(TestEntity.class))).thenReturn(testEntity);

        testManagementService.editTestStatus(testId, testStatusRequestDTO);

        verify(testRepository).findById(testId);
        verify(testRepository).save(any(TestEntity.class));
    }

    @Test
    void editTestStatus_TestNotFound() {
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(DontFoundException.class, () -> testManagementService.editTestStatus(testId, testStatusRequestDTO));
        verify(testRepository).findById(testId);
        verify(testRepository, never()).save(any());
    }

    @Test
    void getAllTests_Success() {
        List<TestEntity> tests = Collections.singletonList(testEntity);
        List<TestTitleModel> titleModels = Collections.singletonList(testTitleModel);

        when(testRepository.findByParticipantsIdsContaining(userId)).thenReturn(tests);
        when(testMapper.toTitleModelList(tests)).thenReturn(titleModels);

        TestsResponseDTO result = testManagementService.getAllTests(userId);

        assertNotNull(result);
        assertEquals("360", result.nameGroup());
        assertEquals(titleModels, result.tests());
        verify(testRepository).findByParticipantsIdsContaining(userId);
        verify(testMapper).toTitleModelList(tests);
    }

    @Test
    void getTest_Success() {
        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(questionRepository.findAllById(testEntity.getQuestionsIds())).thenReturn(Collections.singletonList(questionEntity));
        when(questionMapper.toQuestionTestModelList(any())).thenReturn(Collections.singletonList(questionTestModel));

        TestResponseDTO result = testManagementService.getTest(testId, userId, evaluatedId);

        assertNotNull(result);
        assertEquals(testEntity.getTitle(), result.title());
        assertEquals(evaluatedId, result.evaluatedId());
        assertEquals(userId, result.evaluatorId());
        verify(testRepository).findById(testId);
        verify(questionRepository).findAllById(testEntity.getQuestionsIds());
    }

    @Test
    void getTest_TestNotFound() {
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(DontFoundException.class, () -> testManagementService.getTest(testId, userId, evaluatedId));
        verify(testRepository).findById(testId);
        verify(questionRepository, never()).findAllById(any());
    }

    @Test
    void addTest_Success() {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setSkillsText("test");
        skillEntity.setId(1);
        when(skillRepository.saveAll(any())).thenReturn(List.of(skillEntity));
        when(skillMapper.toSkillEntity(any())).thenReturn(skillEntity);
        when(questionRepository.saveAll(any())).thenReturn(new ArrayList<>(List.of(questionEntity)));
        when(testRepository.save(any(TestEntity.class))).thenReturn(testEntity);
        when(testMapper.toTestEntity(any(TestRequestDTO.class), any(List.class), any(StatusTestEnum.class))).thenReturn(testEntity);
        when(questionMapper.toQuestionEntity(any(QuestionTestModel.class))).thenReturn(questionEntity);

        testManagementService.addTest(testRequestDTO);

        verify(skillRepository).saveAll(any());
        verify(questionRepository).saveAll(any());
        verify(testRepository).save(any(TestEntity.class));
    }

} 