package com.oneonefive.PathNote.repository;

import com.oneonefive.PathNote.entity.Course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // 주소가 특정 지역으로 시작하는 코스 조회
    @Query("SELECT DISTINCT c FROM Course c JOIN c.coursePlaces cp JOIN cp.place p WHERE p.placeAddress LIKE :region%")
    List<Course> findByPlaceAddressStartingWith(@Param("region") String region);

    // 코스에 카테고리가 포함된 코스 조회
    List<Course> findByCourseCategory(String category);

    // 주소와 코스명 모두에 해당하는 코스 조회 (복합 조건 예시)
    @Query("SELECT DISTINCT c FROM Course c JOIN c.coursePlaces cp JOIN cp.place p WHERE p.placeAddress LIKE :region% AND c.courseCategory LIKE %:category%")
    List<Course> findByPlaceAddressStartingWithAndCourseCategory(@Param("region") String region, @Param("category") String category);

}
