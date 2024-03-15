package com.maruhxn.choicearena.domain.favorite.domain;

import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.domain.topic.domain.Topic;
import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 있거나, 없거나의 2가지 경우의 수이므로 기본 필드 외에 어떠한 다른 필드도 필요하지 않음.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicFavorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public TopicFavorite(Member member, Topic topic) {
        this.member = member;
        this.topic = topic;
    }
}