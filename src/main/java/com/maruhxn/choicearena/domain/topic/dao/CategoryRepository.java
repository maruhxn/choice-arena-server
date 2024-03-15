package com.maruhxn.choicearena.domain.topic.dao;

import com.maruhxn.choicearena.domain.topic.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
