package com.maruhxn.choicearena.domain.topic.domain;

import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reference extends BaseEntity {

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String storedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public Reference(String originalName, String storedName, Topic topic) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.topic = topic;
    }
}