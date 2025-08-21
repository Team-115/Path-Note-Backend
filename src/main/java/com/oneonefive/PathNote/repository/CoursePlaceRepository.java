package com.oneonefive.PathNote.repository;

import com.oneonefive.PathNote.entity.CoursePlace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePlaceRepository extends JpaRepository<CoursePlace, Long> {

}
