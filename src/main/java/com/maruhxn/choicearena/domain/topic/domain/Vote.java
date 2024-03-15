package com.maruhxn.choicearena.domain.topic.domain;

import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

    private Boolean isFirst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", referencedColumnName = "id")
    private Member voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public Vote(Boolean isFirst, Member voter, Topic topic) {
        this.isFirst = isFirst;
        this.voter = voter;
        this.topic = topic;
    }
}