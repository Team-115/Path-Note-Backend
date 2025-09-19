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

    @Query(value = """
        SELECT c.*
        FROM courses c
        ORDER BY c.embedding_vector <-> :searchVector :: vector
        LIMIT :limit
        """, nativeQuery = true)
    List<Course> findSimilarCoursesByVector(
        @Param("searchVector") String searchVector,
        @Param("limit") int limit
    );
}
