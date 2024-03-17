package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.member.domain.Role;
import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static com.maruhxn.choicearena.global.common.Constants.ACCESS_TOKEN_HEADER;
import static com.maruhxn.choicearena.global.common.Constants.REFRESH_TOKEN_HEADER;

@Component
public class JwtProvider {

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    private SecretKey secretKey;
    private JwtParser jwtParser;
    public static final String BEARER_PREFIX = "Bearer ";


    public JwtProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtParser = Jwts.parser()
                .verifyWith(this.secretKey)
                .build();
    }

    public TokenDto createJwt(ChoiceArenaOAuth2User choiceArenaOAuth2User) {

        String email = choiceArenaOAuth2User.getEmail();

        String accessToken = generateAccessToken(choiceArenaOAuth2User, new Date());
        String refreshToken = generateRefreshToken(email, new Date());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(ChoiceArenaOAuth2User choiceArenaOAuth2User, Date now) {
        String email = choiceArenaOAuth2User.getEmail();
        String username = choiceArenaOAuth2User.getName();
        String provider = choiceArenaOAuth2User.getProvider();
        ArrayList<? extends GrantedAuthority> authorities =
                (ArrayList<? extends GrantedAuthority>) choiceArenaOAuth2User.getAuthorities();
        return Jwts.builder()
                .subject(email)
                .claim("username", username)
                .claim("provider", provider)
                .claim("role", authorities.get(0).getAuthority())
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

    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }

    public Claims getPayload(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validate(String token) {
        try {
            return getPayload(token)
                    .getExpiration()
                    .after(new Date());
        } catch (SecurityException e) {
            throw new JwtException("검증 정보가 올바르지 않습니다.");
        } catch (MalformedJwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("기한이 만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 토큰입니다.");
        }
    }

    public ChoiceArenaOAuth2User getPrincipal(String accessToken) {
        Claims payload = getPayload(accessToken);
        String email = payload.getSubject();
        String username = payload.get("username").toString();
        String provider = payload.get("provider").toString();
        String role = payload.get("role").toString();

        return ChoiceArenaOAuth2User.of(email, username, provider, Role.valueOf(role));
    }

    public String getBearerTokenToString(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }
}
