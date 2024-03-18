package com.maruhxn.choicearena.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maruhxn.choicearena.domain.auth.api.AuthController;
import com.maruhxn.choicearena.global.auth.application.JwtProvider;
import com.maruhxn.choicearena.global.auth.application.JwtService;
import com.maruhxn.choicearena.global.config.SecurityConfig;
import com.maruhxn.choicearena.global.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
        }
)
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtService jwtService;

    @MockBean
    protected JwtProvider jwtProvider;
}
