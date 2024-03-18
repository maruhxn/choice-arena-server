package com.maruhxn.choicearena.domain.auth.api;

import com.maruhxn.choicearena.global.auth.application.JwtService;
import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.common.dto.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.maruhxn.choicearena.global.common.Constants.REFRESH_TOKEN_HEADER;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @GetMapping("/refresh")
    public ResponseEntity<DataResponse<TokenDto>> refresh(
            HttpServletResponse response,
            @RequestHeader(value = REFRESH_TOKEN_HEADER) String bearerRefreshToken
    ) {
        TokenDto tokenDto = jwtService.refresh(bearerRefreshToken, response);
        return ResponseEntity.ok(DataResponse.of("Token Refresh 성공", tokenDto));
    }
}
