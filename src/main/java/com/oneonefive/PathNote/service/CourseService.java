package com.oneonefive.PathNote.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.repository.CourseRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    // 코스 전체 조회
    @Transactional
    public List<CourseDTO> findCourseAll() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDTO> courseDTOs = new ArrayList<>();

        for (Course course : courses) {

            // (코스-장소 DTO 리스트) 생성
            List<CoursePlaceDTO> coursePlaces = new ArrayList<>();
            for (CoursePlace coursePlace : course.getCoursePlaces()) {
                CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                    coursePlace.getPlace().getPoiId(),
                    coursePlace.getSequenceIndex(),
                    coursePlace.getPlace().getPlaceName(),
                    coursePlace.getPlace().getPlaceCategory(),
                    coursePlace.getPlace().getPlaceAddress(),
                    coursePlace.getPlace().getPlaceCoordinateX(),
                    coursePlace.getPlace().getPlaceCoordinateY(),
                    coursePlace.getEnterTime(),
                    coursePlace.getLeaveTime()
                    );
                coursePlaces.add(coursePlaceDTO);
            }

            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseCategory(),
                course.getCreatedAt(),
                course.getLikeCount(),
                coursePlaces
            );

            // (코스 DTO 리스트)에 (코스 DTO) 추가
            courseDTOs.add(courseDTO);
        }

        // (코스 DTO 리스트) 반환
        return courseDTOs;
    }

    // 코스 단일 조회 (course_id)
    @Transactional
    public CourseDTO findCourseById(Long course_id) {

        // 코스 엔티티 조회
        Course course = courseRepository.findById(course_id).orElse(null);
        CourseDTO courseDTO = new CourseDTO(
            course.getCourseId(),
            course.getCourseName(),
            course.getCourseDescription(),
            course.getCourseCategory(),
            course.getCreatedAt(),
            course.getLikeCount(),
            new ArrayList<>()
        );

        // 코스-장소 DTO 리스트 생성
        List<CoursePlaceDTO> coursePlaces = new ArrayList<>();
        

        // course_id를 가지고있는 코스-장소들을 코스-장소 DTO 리스트에 저장
        for (CoursePlace coursePlace : course.getCoursePlaces()) {
            CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                coursePlace.getPlace().getPoiId(),
                coursePlace.getSequenceIndex(),
                coursePlace.getPlace().getPlaceName(),
                coursePlace.getPlace().getPlaceCategory(),
                coursePlace.getPlace().getPlaceAddress(),
                coursePlace.getPlace().getPlaceCoordinateX(),
                coursePlace.getPlace().getPlaceCoordinateY(),
                coursePlace.getEnterTime(),
                coursePlace.getLeaveTime()
                );
            coursePlaces.add(coursePlaceDTO);
        }

        // 코스 DTO에 코스-장소 DTO 리스트 저장
        courseDTO.setCoursePlaces(coursePlaces);
        return courseDTO;

    }

    // 코스 생성
    @Transactional
    public CourseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        Course createdCourse = new Course();
        createdCourse.setCourseName(courseRequestDTO.getCourse_name());
        createdCourse.setCourseDescription(courseRequestDTO.getCourse_description());
        createdCourse.setCourseCategory(courseRequestDTO.getCourse_category());

        courseRepository.save(createdCourse);

        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_name(createdCourse.getCourseName());
        courseDTO.setCourse_description(createdCourse.getCourseDescription());
        courseDTO.setCourse_category(createdCourse.getCourseCategory());
        
        // 코스 DTO 반환
        return courseDTO;
    }

    // 코스 제거
    @Transactional
    public void deleteCourseById(Long course_id) {
        courseRepository.deleteById(course_id);
    }
    
}
