package com.maruhxn.choicearena.domain.topic.dao;

import com.maruhxn.choicearena.domain.topic.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
