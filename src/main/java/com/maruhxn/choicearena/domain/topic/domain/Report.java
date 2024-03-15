package com.maruhxn.choicearena.domain.topic.domain;

import com.maruhxn.choicearena.domain.member.domain.Member;
import com.maruhxn.choicearena.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @Builder
    public Report(ReportType reportType, String description, Member reporter, Topic topic) {
        this.reportType = reportType;
        this.description = description;
        this.reporter = reporter;
        this.topic = topic;
    }
}