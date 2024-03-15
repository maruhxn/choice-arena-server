package com.maruhxn.choicearena.domain.favorite.dao;

import com.maruhxn.choicearena.domain.favorite.domain.CommentFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentFavoriteRepository extends JpaRepository<CommentFavorite, Long> {
}
