package ru.singularity.evaluation360.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.dto.result.ResultApproveRequestDto;
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionModel;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.exeptions.FalsiesDtoFormatException;
import ru.singularity.evaluation360.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private ResultService resultService;

    TestRequestDTO testRequestDTO;

    QuestionTestModel questionTestModel;

    ResultApproveRequestDto resultApproveRequestDto;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @BeforeEach
    void setUp() {
        User principal = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userEntity);

        questionTestModel = new QuestionTestModel("test", List.of(1));


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

        resultApproveRequestDto = new ResultApproveRequestDto(true, "comment", new ArrayList<>());


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
    void postTest_Success() throws Exception {
        doNothing().when(testManagementService).addTest(any(TestRequestDTO.class));

        mockMvc.perform(post("/admin/addTest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void postTest_BadRequest() throws Exception {
        doThrow(new RuntimeException()).when(testManagementService).addTest(any());

        mockMvc.perform(post("/admin/addTest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getTestAdmin_NotFound() throws Exception {
        when(testManagementService.getTest(anyString())).thenThrow(new BadCredentialsException("Test not found"));

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

    @Test
    void editTest_Success() throws Exception {
        doNothing().when(resultService).editResult(anyString(), any(ResultApproveRequestDto.class));

        mockMvc.perform(put("/admin/result/approve/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultApproveRequestDto)))
                .andExpect(status().isOk());

    }

}
