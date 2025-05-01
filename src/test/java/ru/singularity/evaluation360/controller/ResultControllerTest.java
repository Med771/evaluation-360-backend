package ru.singularity.evaluation360.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.MediaType;
import ru.singularity.evaluation360.dto.result.CommentEditRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;
import ru.singularity.evaluation360.dto.result.model.AnswerTestModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.ResultService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResultControllerTest extends BaseControllerTest {

    @MockBean
    private ResultService resultService;

    @MockBean
    private AuthService authService;

    private User principal;
    private Authentication authentication;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() throws Exception {
        principal = new User("testuser", "password", List.of(new SimpleGrantedAuthority("USER")));
        authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        userEntity = new UserEntity();
        userEntity.setId(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authService.findUserByEmail(anyString())).thenReturn(userEntity);


    }



    @Test
    void getResult_Success() throws Exception {
        String testId = "test123";
        ResultResponseDTO resultResponse = new ResultResponseDTO(
                "test",
                2.0,
                2.0,
                2.0,
                2.0,
                List.of(),
                "test"
        );

        when(resultService.getResultByIndex(anyString(), anyInt())).thenReturn(resultResponse);

        mockMvc.perform(get("/result/{test_id}", testId))
                .andExpect(status().isOk());
    }
    @Test
    void addResult_Success() throws Exception {
        String testId = "test123";
        ResultRequestDTO resultRequest = new ResultRequestDTO(
            1,
            2,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            Arrays.asList(
                new AnswerTestModel(3, "Good answer"),
                new AnswerTestModel(4, "Excellent answer")
            ),
            Arrays.asList(
                new SkillsTestModel(1, 3.5),
                new SkillsTestModel(2, 4.0)
            )
        );

        mockMvc.perform(post("/result/{test_id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resultRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void editResult_Success(){

    }
} 