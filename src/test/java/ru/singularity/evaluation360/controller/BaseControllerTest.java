package ru.singularity.evaluation360.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.singularity.evaluation360.config.JwtCore;
import ru.singularity.evaluation360.service.CustomUserDetailsService;

public abstract class BaseControllerTest {

    @MockBean
    private JwtCore jwtCore;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
