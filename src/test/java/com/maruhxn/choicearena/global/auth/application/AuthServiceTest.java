package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.member.dao.MemberRepository;
import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.domain.member.domain.OAuth2Provider;
import com.maruhxn.choicearena.domain.member.domain.Role;
import com.maruhxn.choicearena.global.auth.model.provider.GoogleUser;
import com.maruhxn.choicearena.util.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Service] - AuthService")
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("OAuth2 유저 정보를 받아 Member Entity를 생성한다.")
    void createOrUpdate() {
        // given
        GoogleUser googleUser = getGoogleUser("test@test.com");

        // when
        Member member = authService.createOrUpdate(googleUser);

        // then
        Member findMember = memberRepository.findAll().get(0);
        assertThat(member).isEqualTo(findMember);
    }

    @Test
    @DisplayName("OAuth2 유저 정보와 일치하는 Member가 이미 존재하는 경우, 해당 Member의 정보를 수정한다.")
    void createOrUpdate_update() {
        // given
        Member existingMember = Member.builder()
                .username("existing")
                .email("test@test.com")
                .role(Role.ROLE_USER)
                .providerId("google_foobarfoobar")
                .provider(OAuth2Provider.GOOGLE)
                .profileImageUrl("existing-google-user-picutre-url")
                .build();
        memberRepository.save(existingMember);
        GoogleUser googleUser = getGoogleUser("test@test.com");

        // when
        authService.createOrUpdate(googleUser);

        // then
        Member findMember = memberRepository.findAll().get(0);
        assertThat(findMember.getUsername()).isEqualTo("tester");
        assertThat(findMember.getProfileImageUrl()).isEqualTo("google-user-picutre-url");
    }

    @Test
    @DisplayName("admin 이메일에 해당하는 계정일 경우, 어드민 권한으로 생성된다.")
    void adminCreate() {
        // given
        GoogleUser googleUser = getGoogleUser("maruhan1016@gmail.com");

        // when
        Member member = authService.createOrUpdate(googleUser);

        // then
        assertThat(member.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    private static GoogleUser getGoogleUser(String email) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "foobarfoobar");
        attributes.put("picture", "google-user-picutre-url");
        attributes.put("email", email);
        attributes.put("name", "tester");
        GoogleUser googleUser = new GoogleUser(attributes, "google");
        return googleUser;
    }
}