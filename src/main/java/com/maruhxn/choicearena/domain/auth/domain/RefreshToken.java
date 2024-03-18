package com.maruhxn.choicearena.domain.auth.domain;

import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @NotBlank
    private String payload;

    @NotBlank
    private String email;

    @Builder
    public RefreshToken(String payload, String email) {
        this.payload = payload;
        this.email = email;
    }

    public RefreshToken updatePayload(String payload) {
        this.payload = payload;
        return this;
    }
}