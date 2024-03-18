package com.maruhxn.choicearena.global.auth.application;

import com.maruhxn.choicearena.domain.member.dao.MemberRepository;
import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.domain.member.domain.OAuth2Provider;
import com.maruhxn.choicearena.domain.member.domain.Role;
import com.maruhxn.choicearena.global.auth.model.provider.OAuth2ProviderUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    public Member createOrUpdate(OAuth2ProviderUser oAuth2ProviderUser) {
        Member member = null;
        Optional<Member> optionalMember = memberRepository.findByEmail(oAuth2ProviderUser.getEmail());

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
            member.update(oAuth2ProviderUser);
        } else {
            member = this.register(oAuth2ProviderUser);
        }

        if (member == null) throw new IllegalStateException("멤버 정보를 찾을 수 없습니다.");

        return member;
    }

    private Member register(OAuth2ProviderUser oAuth2ProviderUser) {

        Role role = oAuth2ProviderUser.getEmail().equals("maruhan1016@gmail.com")
                ? Role.ROLE_ADMIN
                : Role.ROLE_USER;

        Member member = Member.builder()
                .username(oAuth2ProviderUser.getUsername())
                .email(oAuth2ProviderUser.getEmail())
                .provider(OAuth2Provider.valueOf(oAuth2ProviderUser.getProvider().toUpperCase()))
                .providerId(oAuth2ProviderUser.getProviderId())
                .role(role)
                .build();

        memberRepository.save(member);

        return member;
    }
}
