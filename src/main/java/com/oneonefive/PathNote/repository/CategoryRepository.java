package com.oneonefive.PathNote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oneonefive.PathNote.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsById(Long id);
    boolean existsByContent(String content);
    Category findCategoryByContent(String content);

    //
    @Query("SELECT DISTINCT c FROM Category c WHERE c.content LIKE :content%")
    List<Category> findByContentStartingWith(@Param("content") String content);
}
