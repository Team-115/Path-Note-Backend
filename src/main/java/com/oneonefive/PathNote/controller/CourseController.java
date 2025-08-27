package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
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
    public List<CourseDTO> getAllCourses() {
        return courseService.findCourseAll();
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
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST /api/courses
    // 코스 신규 등록
    // 코스 완성 후 업로드 시 등록
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseRequestDTO courseRequestDTO) {
        CourseDTO courseDTO = courseService.createCourse(courseRequestDTO);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    // PUT /api/courses/{course_id}
    // 코스 수정
    @PutMapping("/{course_id}")
    public ResponseEntity<CourseDTO> editCourseById(@PathVariable Long course_id, @RequestBody CourseRequestDTO courseRequestDTO) {
        CourseDTO courseDTO = courseService.editCourse(course_id, courseRequestDTO);
        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    // DELETE /api/courses/{course_id}
    // 코스 삭제
    // 만들었던 코스를 삭제, 본인이 만든 코스만 삭제 가능
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourseById(@PathVariable Long course_id) {
        courseService.deleteCourseById(course_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
