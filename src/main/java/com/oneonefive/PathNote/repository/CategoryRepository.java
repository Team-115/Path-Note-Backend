package com.oneonefive.PathNote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oneonefive.PathNote.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsById(Long id);
    boolean existsByContent(String content);
    Category findCategoryByContent(String content);
}
