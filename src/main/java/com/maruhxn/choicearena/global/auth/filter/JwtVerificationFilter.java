package com.maruhxn.choicearena.global.auth.filter;

import com.maruhxn.choicearena.global.auth.application.JwtProvider;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDE_URL =
            List.of("/login/oauth2/code/google",
                    "/login/oauth2/code/naver",
                    "/login/oauth2/code/kakao",
                    "/oauth2/authorization/google",
                    "/oauth2/authorization/naver",
                    "/oauth2/authorization/kakao",
                    "/api/auth/refresh");
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtProvider.resolveAccessToken(request);

        // validation
        if (!StringUtils.hasText(accessToken)) {
            jwtProvider.validate(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationToContext(accessToken);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    private void setAuthenticationToContext(String accessToken) {
        ChoiceArenaOAuth2User choiceArenaOAuth2User = jwtProvider.getPrincipal(accessToken);
        OAuth2AuthenticationToken authentication =
                new OAuth2AuthenticationToken(choiceArenaOAuth2User, choiceArenaOAuth2User.getAuthorities(), choiceArenaOAuth2User.getProvider());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
