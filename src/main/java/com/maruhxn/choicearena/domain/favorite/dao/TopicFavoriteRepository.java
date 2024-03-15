package com.maruhxn.choicearena.domain.favorite.dao;

import com.maruhxn.choicearena.domain.favorite.domain.TopicFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicFavoriteRepository extends JpaRepository<TopicFavorite, Long> {
}
