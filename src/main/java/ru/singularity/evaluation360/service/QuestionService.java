package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.QuestionsResponseDTO;
import ru.singularity.evaluation360.dto.test.TestRequestDTO;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.QuestionEntity;
import ru.singularity.evaluation360.mapper.QuestionMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.QuestionRepository;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    public QuestionsResponseDTO getAllQuestions() {
        return new QuestionsResponseDTO(questionMapper.toQuestionModelList(questionRepository.findAll()));
    }
}
