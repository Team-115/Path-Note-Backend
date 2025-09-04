package com.oneonefive.PathNote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oneonefive.PathNote.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByCourse_CourseId(Long courseId);
}
