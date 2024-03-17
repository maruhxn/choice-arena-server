package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.auth.dao.RefreshTokenRepository;
import com.maruhxn.choicearena.domain.auth.domain.RefreshToken;
import com.maruhxn.choicearena.domain.member.dao.MemberRepository;
import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.global.auth.dto.TokenDto;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import com.maruhxn.choicearena.global.error.ErrorCode;
import com.maruhxn.choicearena.global.error.exception.NotFoundException;
import com.maruhxn.choicearena.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public void saveRefreshToken(ChoiceArenaOAuth2User choiceArenaOAuth2User, TokenDto tokenDto) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(choiceArenaOAuth2User.getEmail());
        refreshToken.ifPresentOrElse(
                // 있다면 새토큰 발급후 업데이트
                token -> {
                    token.updatePayload(tokenDto.getRefreshToken());
                },
                // 없다면 새로 만들고 DB에 저장
                () -> {
                    RefreshToken newToken =
                            new RefreshToken(tokenDto.getRefreshToken(), choiceArenaOAuth2User.getEmail());
                    refreshTokenRepository.save(newToken);
                });
    }

    public TokenDto refresh(
            String bearerRefreshToken,
            HttpServletResponse response
    ) {
        String refreshToken = jwtProvider.getBearerTokenToString(bearerRefreshToken);

        if (!jwtProvider.validate(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken findRefreshToken = refreshTokenRepository.findByPayload(refreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));

        Member findMember = memberRepository.findByEmail(findRefreshToken.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        // access token 과 refresh token 모두를 재발급
        ChoiceArenaOAuth2User choiceArenaOAuth2User = ChoiceArenaOAuth2User.of(
                findMember.getEmail(),
                findMember.getUsername(),
                String.valueOf(findMember.getProvider()),
                findMember.getRole()
        );

        String newAccessToken = jwtProvider.generateAccessToken(choiceArenaOAuth2User, new Date());
        String newRefreshToken = jwtProvider.generateRefreshToken(choiceArenaOAuth2User.getEmail(), new Date());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        this.saveRefreshToken(choiceArenaOAuth2User, tokenDto);

        jwtProvider.setHeader(response, tokenDto);

        return tokenDto;
    }

    public void logout(String bearerRefreshToken) {
        String refreshToken = jwtProvider.getBearerTokenToString(bearerRefreshToken);
        String email = jwtProvider.getPayload(refreshToken).getSubject();
        refreshTokenRepository.deleteAllByEmail(email);
    }
}