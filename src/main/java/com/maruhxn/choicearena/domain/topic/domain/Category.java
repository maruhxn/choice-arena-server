package com.maruhxn.choicearena.domain.topic.domain;

import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }
}
