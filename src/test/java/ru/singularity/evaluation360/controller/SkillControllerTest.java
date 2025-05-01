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
import ru.singularity.evaluation360.dto.test.SkillRequestDTO;
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.entity.UserEntity;
import ru.singularity.evaluation360.service.AuthService;

import ru.singularity.evaluation360.service.CustomUserDetailsService;
import ru.singularity.evaluation360.service.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest extends BaseControllerTest {

    @MockBean
    private SkillService skillService;

    @MockBean
    private AuthService authService;
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


    }

    @Test
    void addSkills_Success() throws Exception {
        List<SkillRequestDTO> skillRequests = Arrays.asList(
            new SkillRequestDTO("Skill 1"),
            new SkillRequestDTO("Skill 2")
        );
        List<SkillEntity> skillEntities = Arrays.asList(
            new SkillEntity(),
            new SkillEntity()
        );
        when(skillService.addSkills(any())).thenReturn(skillEntities);

        mockMvc.perform(post("/skill/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skillRequests)))
                .andExpect(status().isOk());
    }

    @Test
    void getSkills_Success() throws Exception {
        List<SkillEntity> skills = Arrays.asList(
            new SkillEntity(),
            new SkillEntity()
        );
        when(skillService.getSkills()).thenReturn(skills);

        mockMvc.perform(get("/skill/skills"))
                .andExpect(status().isOk());
    }
} 