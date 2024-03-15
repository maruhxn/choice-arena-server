package com.maruhxn.choicearena.domain.member.domain;

import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(length = 10, nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    @Column(unique = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROLE_USER'")
    private Role role;

    @Builder
    public Member(String username, String email, OAuth2Provider provider, String providerId, Role role) {
        this.username = username;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }
}