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
import ru.singularity.evaluation360.dto.test.*;
import ru.singularity.evaluation360.dto.test.model.QuestionTestModel;
import ru.singularity.evaluation360.dto.test.model.TestTitleModel;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.TypeTestEnum;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.EvaluationService;
import ru.singularity.evaluation360.service.TestManagementService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TestControllerTest extends BaseControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private TestManagementService testManagementService;

    @MockBean
    private EvaluationService evaluationService;

    TestRequestDTO testRequestDTO;

    QuestionTestModel questionTestModel;

    TestResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        User principal = new User("testuser", "password", List.of(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authService.findUserByEmail(anyString())).thenReturn(userEntity);

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

        testResponseDTO = new TestResponseDTO(
                "test",
                1L,
                1L,
                List.of(questionTestModel)
        );
    }

    @Test
    void getTests_Success() throws Exception {
        TestsResponseDTO testsResponse = new TestsResponseDTO("test", List.of());
        when(testManagementService.getAllTests(anyInt())).thenReturn(testsResponse);

        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }

    @Test
    void getTestMenu_Success() throws Exception {
        TestMenuResponseDTO menuResponse = new TestMenuResponseDTO(
                "test",
                true,
                true,
                true,
                true,
                true,
                List.of(),
                List.of(),
                true
        );
        when(evaluationService.getTestMenu(anyString(), anyInt())).thenReturn(menuResponse);

        mockMvc.perform(get("/test/menu/test123"))
                .andExpect(status().isOk());
    }

    @Test
    void getTest_Success() throws Exception {
        when(testManagementService.getTest(anyString(), anyInt(), anyInt())).thenReturn(testResponseDTO);

        mockMvc.perform(get("/test/test123/1"))
                .andExpect(status().isOk());
    }





} 