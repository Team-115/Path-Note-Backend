package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    @Autowired
    private final CourseService courseService;

    // GET /api/courses
    // 코스 전체 조회
    // 코스 페이지 열람시 우측 컴포넌트에 표시
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(
        @RequestParam(name = "region", required = false) String region,
        @RequestParam(name = "category", required = false) String category) {
        List<CourseDTO> courseDTOs = courseService.findCourseAll(region, category);
        
        if (courseDTOs == null || courseDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        }

    }

    // GET /api/courses/{course_id}
    // 코스 단일 조회
    // 코스 페이지 열람 후 우측 컴포넌트 클릭 시 조회
    @GetMapping("/{course_id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long course_id) {
        CourseDTO courseDTO = courseService.findCourseById(course_id);

        if (courseDTO != null) {
            return new ResponseEntity<>(courseDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST /api/courses
    // 코스 신규 등록
    // 코스 완성 후 업로드 시 등록
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseRequestDTO courseRequestDTO, @AuthenticationPrincipal User user) {
        courseRequestDTO.setUser_id(user.getUserId());
        CourseDTO courseDTO = courseService.createCourse(courseRequestDTO);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    // PUT /api/courses/{course_id}
    // 코스 수정
    @PutMapping("/{course_id}")
    public ResponseEntity<CourseDTO> editCourseById(@PathVariable Long course_id, @RequestBody CourseRequestDTO courseRequestDTO, @AuthenticationPrincipal User user) {
        courseRequestDTO.setUser_id(user.getUserId());
        CourseDTO courseDTO = courseService.editCourse(course_id, courseRequestDTO);
        if (courseDTO != null) {
            
            return new ResponseEntity<>(courseDTO, HttpStatus.OK);    
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/courses/{course_id}
    // 코스 삭제
    // 만들었던 코스를 삭제, 본인이 만든 코스만 삭제 가능
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourseById(@PathVariable Long course_id, @AuthenticationPrincipal User user) {
        if (courseService.deleteCourseById(course_id, user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/courses/search
    // 키워드 기반 코스 검색 (임베딩 벡터 유사도 활용)
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCoursesByKeyword(
        @RequestParam(name = "keyword") String keyword,
        @RequestParam(name = "limit", defaultValue = "10") int limit) {
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        List<CourseDTO> courseDTOs = courseService.searchCoursesByKeyword(keyword.trim(), limit);
        
        if (courseDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
        }
    }

    // POST /api/courses/{course_id}/update-embedding
    // 특정 코스의 임베딩 벡터 업데이트 (관리용)
    @PostMapping("/{course_id}/update-embedding")
    public ResponseEntity<Void> updateCourseEmbedding(@PathVariable Long course_id, @AuthenticationPrincipal User user) {
        boolean success = courseService.updateCourseEmbedding(course_id);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/courses/null-embeddings
    // 임베딩이 null인 코스들 조회 (Python 스케줄링용)
    @GetMapping("/null-embeddings")
    public ResponseEntity<List<CourseDTO>> getCoursesWithNullEmbedding() {
        List<CourseDTO> courseDTOs = courseService.getCoursesWithNullEmbedding();
        return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
    }

    // POST /api/courses/{course_id}/embedding
    // 코스 임베딩 벡터 직접 업데이트 (Python에서 사용)
    @PostMapping("/{course_id}/embedding")
    public ResponseEntity<Void> updateCourseEmbeddingWithVector(
        @PathVariable Long course_id, 
        @RequestBody List<Double> embedding) {
        
        boolean success = courseService.updateCourseEmbeddingWithVector(course_id, embedding);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
