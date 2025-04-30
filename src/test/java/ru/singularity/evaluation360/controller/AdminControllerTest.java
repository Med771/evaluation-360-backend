package ru.singularity.evaluation360.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.test.QuestionsResponseDTO;
import ru.singularity.evaluation360.dto.test.TestStatusRequestDTO;
import ru.singularity.evaluation360.dto.test.TestViewResponseDTO;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.FalsiesDtoFormatException;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.QuestionService;
import ru.singularity.evaluation360.service.TestManagementService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest extends BaseControllerTest {

    @MockBean
    private QuestionService questionService;

    @MockBean
    private TestManagementService testManagementService;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        User principal = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authService.findUserByEmail(anyString())).thenReturn(userEntity);
    }

    @Test
    void getQuestions_Success() throws Exception {
        QuestionsResponseDTO questionsResponse = new QuestionsResponseDTO(List.of(
                new QuestionModel("id", "test", List.of(2))
        ));
        when(questionService.getAllQuestions()).thenReturn(questionsResponse);

        mockMvc.perform(get("/admin/questions"))
                .andExpect(status().isOk())  // Проверка на успешный статус
                .andExpect(jsonPath("$.questions[0].id").value("id"))
                .andExpect(jsonPath("$.questions[0].questionText").value("test"));
    }

    @Test
    void getTestAdmin_Success() throws Exception {
        TestViewResponseDTO testViewResponse = new TestViewResponseDTO(
                "title",
                TypeTestEnum.FULL,
                StatusTestEnum.CREATED,
                1L,
                2L,
                3L,
                List.of(new RespondentModel(1, RoleUserEnum.USER, "test", 1)),
                List.of(new QuestionModel("id", "test", List.of(2))),
                1,1,1,1
        );
        when(testManagementService.getTest(anyString())).thenReturn(testViewResponse);

        mockMvc.perform(get("/admin/test/test123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.type").value("FULL"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.respondents[0].fullName").value("test"));
    }

    @Test
    void updateTestStatus_Success() throws Exception {
        TestStatusRequestDTO statusRequest = new TestStatusRequestDTO(StatusTestEnum.CREATED);

        doNothing().when(testManagementService).
                editTestStatus("test", new TestStatusRequestDTO(StatusTestEnum.CREATED));

        mockMvc.perform(put("/admin/status/test123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getQuestions_EmptyList() throws Exception {
        // Мокаем пустой список вопросов
        QuestionsResponseDTO questionsResponse = new QuestionsResponseDTO(List.of());
        when(questionService.getAllQuestions()).thenReturn(questionsResponse);

        mockMvc.perform(get("/admin/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions").isEmpty());
    }

    @Test
    void getTestAdmin_NotFound() throws Exception {
        when(testManagementService.getTest(anyString())).thenThrow(new DontFoundException("Test not found"));

        mockMvc.perform(get("/admin/test/invalidTestId"))
                .andExpect(status().isNotFound());  // Проверка на сообщение ошибки
    }

    @Test
    void updateTestStatus_InvalidStatus() throws Exception {
        doThrow(new FalsiesDtoFormatException("")).when(testManagementService).editTestStatus(anyString(), any(TestStatusRequestDTO.class));

        TestStatusRequestDTO invalidStatusRequest = new TestStatusRequestDTO(null);  // Некорректный статус

        mockMvc.perform(put("/admin/status/test123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStatusRequest)))
                .andExpect(status().isBadRequest());
    }
}
