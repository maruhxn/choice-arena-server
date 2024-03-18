package com.maruhxn.choicearena.global.auth.model;

import com.maruhxn.choicearena.domain.member.domain.Role;
import com.maruhxn.choicearena.global.auth.model.provider.OAuth2ProviderUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChoiceArenaOAuth2User implements OAuth2User, Serializable {

    private String provider;
    private Map<String, Object> attributes;
    private Role role;
    private String username;
    private final String email;

    public ChoiceArenaOAuth2User(String email, String username, String provider, Role role) {
        this.provider = provider;
        this.role = role;
        this.username = username;
        this.email = email;
    }

    public ChoiceArenaOAuth2User(Role role, OAuth2ProviderUser oAuth2ProviderUser) {
        this.provider = oAuth2ProviderUser.getProvider();
        this.role = role;
        this.attributes = oAuth2ProviderUser.getAttributes();
        this.username = oAuth2ProviderUser.getUsername();
        this.email = oAuth2ProviderUser.getEmail();
    }

    public static ChoiceArenaOAuth2User of(String email, String username, String provider, Role role) {
        return new ChoiceArenaOAuth2User(email, username, provider, role);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.name());
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public String getEmail() {
        return email;
    }

    public String getProvider() {
        return provider;
    }
}
