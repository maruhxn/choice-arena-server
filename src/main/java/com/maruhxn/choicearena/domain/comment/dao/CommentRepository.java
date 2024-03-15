package com.maruhxn.choicearena.domain.comment.dao;

import com.maruhxn.choicearena.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
