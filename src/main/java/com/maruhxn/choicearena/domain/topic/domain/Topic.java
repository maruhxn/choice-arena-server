package com.maruhxn.choicearena.domain.topic.domain;

import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Topic extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String firstChoice;

    @Column(nullable = false)
    private String secondChoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Builder
    public Topic(String description, String firstChoice, String secondChoice, Member author, Category category) {
        this.description = description;
        this.firstChoice = firstChoice;
        this.secondChoice = secondChoice;
        this.author = author;
        this.category = category;
    }
}