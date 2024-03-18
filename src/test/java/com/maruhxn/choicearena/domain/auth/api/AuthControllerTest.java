package com.maruhxn.choicearena.domain.auth.api;

import com.maruhxn.choicearena.global.common.Constants;
import com.maruhxn.choicearena.global.error.ErrorCode;
import com.maruhxn.choicearena.util.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] - AuthController")
class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("Access Token의 refresh 성공 시 200을 반환한다.")
    @Test
    @WithMockUser
    void refresh() throws Exception {

        // When / Then
        mockMvc.perform(
                        get("/api/auth/refresh")
                                .header(Constants.REFRESH_TOKEN_HEADER, "bearerRefreshToken")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("OK"))
                .andExpect(jsonPath("message").value("Token Refresh 성공"))
                .andDo(print());
    }

    @DisplayName("refresh 시도할 때 refresh token을 넘겨주지 않은 경우 400을 반환한다.")
    @Test
    @WithMockUser
    void refreshWithoutRefreshToken() throws Exception {

        // When / Then
        mockMvc.perform(
                        get("/api/auth/refresh")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(ErrorCode.EMPTY_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("message").value(ErrorCode.EMPTY_REFRESH_TOKEN.getMessage()));
    }
}