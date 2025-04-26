package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.singularity.evaluation360.dto.test.QuestionsResponseDTO;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.mapper.QuestionMapper;
import ru.singularity.evaluation360.repository.QuestionRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionService questionService;

    private QuestionEntity questionEntity1;
    private QuestionEntity questionEntity2;
    private QuestionModel questionModel1;
    private QuestionModel questionModel2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        questionService = new QuestionService(questionRepository, questionMapper);

        questionEntity1 = new QuestionEntity();
        questionEntity1.setId("test");
        questionEntity1.setQuestionText("test");

        questionEntity2 = new QuestionEntity();
        questionEntity2.setId("test1");
        questionEntity2.setQuestionText("test1");

        questionModel1 = new QuestionModel("test", "test", List.of(1, 2));
        questionModel2 = new QuestionModel("test1", "test1", List.of(3, 4));
    }

    @Test
    void getAllQuestions_Success() {
        List<QuestionEntity> questions = Arrays.asList(questionEntity1, questionEntity2);
        List<QuestionModel> questionModels = Arrays.asList(questionModel1, questionModel2);

        when(questionRepository.findAll()).thenReturn(questions);
        when(questionMapper.toQuestionModelList(questions)).thenReturn(questionModels);

        QuestionsResponseDTO result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(questionModels, result.questions());
        verify(questionRepository).findAll();
        verify(questionMapper).toQuestionModelList(questions);
    }

    @Test
    void getAllQuestions_EmptyList() {
        List<QuestionEntity> emptyList = List.of();
        List<QuestionModel> emptyModelList = List.of();

        when(questionRepository.findAll()).thenReturn(emptyList);
        when(questionMapper.toQuestionModelList(emptyList)).thenReturn(emptyModelList);

        QuestionsResponseDTO result = questionService.getAllQuestions();

        assertNotNull(result);
        assertTrue(result.questions().isEmpty());
        verify(questionRepository).findAll();
        verify(questionMapper).toQuestionModelList(emptyList);
    }
} 