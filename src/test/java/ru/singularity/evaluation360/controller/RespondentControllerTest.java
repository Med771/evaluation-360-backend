package ru.singularity.evaluation360.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.singularity.evaluation360.dto.respondent.RespondentsRequestDTO;
import ru.singularity.evaluation360.dto.respondent.RespondentsResponseDTO;
import ru.singularity.evaluation360.dto.respondent.model.RespondentModel;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;
import ru.singularity.evaluation360.service.AuthService;
import ru.singularity.evaluation360.service.CustomUserDetailsService;
import ru.singularity.evaluation360.service.RespondentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RespondentController.class)
@AutoConfigureMockMvc(addFilters = false)
class RespondentControllerTest extends BaseControllerTest {

    @MockBean
    private RespondentService respondentService;

    @MockBean
    private AuthService authService;

    private RespondentsRequestDTO requestDTO;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        User principal = new User("testuser", "password", List.of(new SimpleGrantedAuthority("USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(userEntity);
        requestDTO = new RespondentsRequestDTO(Arrays.asList(2, 3));
    }

    @Test
    void getRespondents_Success() throws Exception {
        RespondentsResponseDTO respondentsResponse = new RespondentsResponseDTO(
                1,
                1,
                1,
                1,
                List.of(new RespondentModel(1, RoleUserEnum.USER,
                        "test", 1))
        );
        when(respondentService.getRespondents(anyString())).thenReturn(respondentsResponse);

        mockMvc.perform(get("/respondent/test123"))
                .andExpect(status().isOk());
    }

    @Test
    void createRespondent_Success() throws Exception {
        doNothing().when(respondentService).setRespondents(anyInt(), anyString(), any(RespondentsRequestDTO.class));

        mockMvc.perform(post("/respondent/test123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void createRespondent_BadRequest() throws Exception {
        doThrow(new RuntimeException()).when(respondentService).setRespondents(anyInt(), anyString(), any());

        mockMvc.perform(post("/respondent/test123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
} 