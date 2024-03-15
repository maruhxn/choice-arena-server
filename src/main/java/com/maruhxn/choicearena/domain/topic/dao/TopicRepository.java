package com.maruhxn.choicearena.domain.topic.dao;

import com.maruhxn.choicearena.domain.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
