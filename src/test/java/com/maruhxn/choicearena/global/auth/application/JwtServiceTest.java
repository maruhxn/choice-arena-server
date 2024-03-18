package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.auth.dao.RefreshTokenRepository;
import com.maruhxn.choicearena.domain.auth.domain.RefreshToken;
import com.maruhxn.choicearena.domain.member.dao.MemberRepository;
import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.domain.member.domain.OAuth2Provider;
import com.maruhxn.choicearena.domain.member.domain.Role;
import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import com.maruhxn.choicearena.global.common.Constants;
import com.maruhxn.choicearena.global.error.ErrorCode;
import com.maruhxn.choicearena.global.error.exception.NotFoundException;
import com.maruhxn.choicearena.global.error.exception.UnauthorizedException;
import com.maruhxn.choicearena.util.IntegrationTestSupport;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Service] - JwtService")
class JwtServiceTest extends IntegrationTestSupport {

    @Autowired
    JwtService jwtService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @DisplayName("유저 이메일을 바탕으로 조회한 refresh token이 없다면 새롭게 저장한다.")
    @Test
    void saveRefreshToken() {
        // Given
        createMember();
        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();

        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        // When
        jwtService.saveRefreshToken(choiceArenaOAuth2User, tokenDto);

        // Then
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByEmail(choiceArenaOAuth2User.getEmail());
        assertThat(optionalRefreshToken.isPresent()).isTrue();
    }

    @DisplayName("유저 정보를 바탕으로 조회한 refresh token이 있다면 전달받은 값으로 덮어씌운다.")
    @Test
    void updateRefreshToken() {
        // Given
        RefreshToken refreshToken = RefreshToken.builder()
                .email("test@test.com")
                .payload("refreshToken")
                .build();
        refreshTokenRepository.save(refreshToken);

        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();

        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("newRefreshToken")
                .build();

        // When
        jwtService.saveRefreshToken(choiceArenaOAuth2User, tokenDto);

        // Then
        RefreshToken findRefreshToken = refreshTokenRepository.findByEmail(choiceArenaOAuth2User.getEmail()).get();
        assertThat(findRefreshToken.getPayload()).isEqualTo(tokenDto.getRefreshToken());
    }

    @DisplayName("refresh token이 유효하다면 access token과 refresh token을 새로 발급한다.")
    @Test
    void refresh() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();

        String rawRefreshToken = jwtProvider.generateRefreshToken(choiceArenaOAuth2User.getEmail(), new Date());
        RefreshToken refreshToken = RefreshToken.builder()
                .email(member.getEmail())
                .payload(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = JwtProvider.BEARER_PREFIX + rawRefreshToken;
        // When
        TokenDto tokenDto = jwtService.refresh(bearerRefreshToken, response);
        // Then
        assertThat(response.getHeader(Constants.ACCESS_TOKEN_HEADER)).isEqualTo(JwtProvider.BEARER_PREFIX + tokenDto.getAccessToken());
        assertThat(response.getHeader(Constants.REFRESH_TOKEN_HEADER)).isEqualTo(JwtProvider.BEARER_PREFIX + tokenDto.getRefreshToken());
    }

    @DisplayName("refreshToken이 만료되었다면 401 에러를 반환한다.")
    @Test
    void refreshWithInvalidRefreshToken() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();
        LocalDateTime now = LocalDateTime.of(2024, 1, 18, 10, 0);
        String rawRefreshToken = jwtProvider.generateRefreshToken(choiceArenaOAuth2User.getEmail(), Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        RefreshToken refreshToken = RefreshToken.builder()
                .email(member.getEmail())
                .payload(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = JwtProvider.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("기한이 만료된 토큰입니다.");
    }

    @DisplayName("refreshToken이 데이터베이스 존재하지 않다면 에러를 반환한다.")
    @Test
    void refreshFailWhenNoRefreshToken() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        Member member = createMember();
        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();

        String rawRefreshToken = jwtProvider.generateRefreshToken(choiceArenaOAuth2User.getEmail(), new Date());

        String bearerRefreshToken = JwtProvider.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("refreshToken에 대응되는 유저 정보가 데이터베이스 존재하지 않다면 에러를 반환한다.")
    @Test
    void refreshFailWhenNoMember() {
        // Given
        HttpServletResponse response = new MockHttpServletResponse();
        ChoiceArenaOAuth2User choiceArenaOAuth2User = getChoiceArenaOAuth2User();

        String rawRefreshToken = jwtProvider.generateRefreshToken(choiceArenaOAuth2User.getEmail(), new Date());

        RefreshToken refreshToken = RefreshToken.builder()
                .email(choiceArenaOAuth2User.getEmail())
                .payload(rawRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);

        String bearerRefreshToken = JwtProvider.BEARER_PREFIX + rawRefreshToken;
        // When / Then
        assertThatThrownBy(() -> jwtService.refresh(bearerRefreshToken, response))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
    }

    private static ChoiceArenaOAuth2User getChoiceArenaOAuth2User() {
        ChoiceArenaOAuth2User choiceArenaOAuth2User =
                ChoiceArenaOAuth2User.of("test@test.com", "tester", "google", Role.ROLE_USER);
        return choiceArenaOAuth2User;
    }

    private Member createMember() {
        Member existingMember = Member.builder()
                .username("tester")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .providerId("google_foobarfoobar")
                .provider(OAuth2Provider.GOOGLE)
                .profileImageUrl("google-user-picutre-url")
                .build();
        return memberRepository.save(existingMember);
    }
}