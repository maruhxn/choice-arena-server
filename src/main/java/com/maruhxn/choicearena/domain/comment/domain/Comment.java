package com.maruhxn.choicearena.domain.comment.domain;

import com.maruhxn.choicearena.domain.favorite.domain.CommentFavorite;
import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.domain.topic.domain.Topic;
import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id", referencedColumnName = "id")
    private Comment replyTo;

    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentFavorite> favorites = new ArrayList<>();

    private String groupId;

    @Builder
    public Comment(String text, Member author, Topic topic, String groupId) {
        this.text = text;
        this.author = author;
        this.topic = topic;
        this.groupId = groupId;
    }

    // 연관관계 메서드 //
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.groupId = this.groupId;
        reply.replyTo = this;
    }

    public void updateText(String text) {
        this.text = text;
    }
}