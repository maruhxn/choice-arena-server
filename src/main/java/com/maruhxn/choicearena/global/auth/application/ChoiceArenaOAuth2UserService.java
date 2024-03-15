package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.global.auth.model.ChoiceArenaOAuth2User;
import com.maruhxn.choicearena.global.auth.model.provider.GoogleUser;
import com.maruhxn.choicearena.global.auth.model.provider.KakaoUser;
import com.maruhxn.choicearena.global.auth.model.provider.NaverUser;
import com.maruhxn.choicearena.global.auth.model.provider.OAuth2ProviderUser;
import com.maruhxn.choicearena.global.error.ErrorCode;
import com.maruhxn.choicearena.global.error.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChoiceArenaOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest); // 인가 서버와 통신해서 실제 사용자 정보를 가져옴

        // GOOGLE, NAVER, KAKAO
        OAuth2ProviderUser oAuth2ProviderUser = this.getOAuth2ProviderUser(clientRegistration, oAuth2User);
        if (oAuth2ProviderUser == null) throw new BadRequestException(ErrorCode.BAD_REQUEST);

        // 회원가입
        Member member = authService.createOrUpdate(oAuth2ProviderUser);
        return new ChoiceArenaOAuth2User(member.getRole(), oAuth2ProviderUser);
    }

    private OAuth2ProviderUser getOAuth2ProviderUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (registrationId.equals("google")) {
            return new GoogleUser(attributes, registrationId);
        } else if (registrationId.equals("naver")) {
            return new NaverUser(attributes, registrationId);
        } else if (registrationId.equals("kakao")) {
            return new KakaoUser(attributes, registrationId);
        }

        return null;
    }


}
