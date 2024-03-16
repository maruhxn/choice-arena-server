package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private SecretKey secretKey;
    private JwtParser jwtParser;
    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh";
    public static final String BEARER_PREFIX = "Bearer ";


    public JwtProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtParser = Jwts.parser()
                .verifyWith(this.secretKey)
                .build();
    }

    public TokenDto createJwt(ChoiceArenaOAuth2User choiceArenaOAuth2User) {

        String email = choiceArenaOAuth2User.getEmail();
        Collection<? extends GrantedAuthority> authorities = choiceArenaOAuth2User.getAuthorities();

        String accessToken = generateAccessToken(email, authorities, new Date());
        String refreshToken = generateRefreshToken(email, new Date());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(String email, Collection<? extends GrantedAuthority> authorities, Date now) {
        return Jwts.builder()
                .subject(email)
                .claim("email", email)
                .claim("roles", authorities)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String email, Date now) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(ACCESS_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getAccessToken());
        response.addHeader(REFRESH_TOKEN_HEADER, BEARER_PREFIX + tokenDto.getRefreshToken());
    }
}
