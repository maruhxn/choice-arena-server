package com.maruhxn.choicearena.global.auth.handler;

import com.maruhxn.choicearena.global.auth.application.JwtProvider;
import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.url}")
    private String clientUrl;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        ChoiceArenaOAuth2User principal = (ChoiceArenaOAuth2User) authentication.getPrincipal();
        TokenDto tokenDto = jwtProvider.createJwt(principal);
        String targetUri = createUri(tokenDto, principal.getName());

        jwtProvider.setHeader(response, tokenDto);
        response.sendRedirect(targetUri);
    }

    private String createUri(TokenDto tokenDto, String username) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("username", username);
        queryParams.add("access_token", tokenDto.getAccessToken());
        queryParams.add("refresh_token", tokenDto.getRefreshToken());

        return UriComponentsBuilder
                .fromHttpUrl(clientUrl + "/oauth2")
                .queryParams(queryParams)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }


}
