package com.maruhxn.choicearena.domain.favorite.domain;

import com.maruhxn.choicearena.domain.comment.domain.Comment;
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
public class CommentFavorite extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @Builder
    public CommentFavorite(Member member, Comment comment) {
        this.member = member;
        this.comment = comment;
    }
}