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

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    // 코스 전체 조회
    public List<Course> findCourseAll() {
        return courseRepository.findAll();
    }

    // 코스 단일 조회 (course_id)
    public CourseDTO findCourseById(Long course_id) {

        // 코스 엔티티 조회
        Course course = courseRepository.findById(course_id).orElse(null);
        CourseDTO courseDTO = new CourseDTO(
            course.getCourseId(),
            course.getCourse_name(),
            course.getCourse_description(),
            course.getCourse_category(),
            course.getCreated_at(),
            course.getLikeCount(),
            new ArrayList<>()
        );

        // 코스-장소 DTO 리스트 생성
        List<CoursePlaceDTO> coursePlaces = new ArrayList<>();
        

        if (course != null) {
            // course_id를 가지고있는 코스-장소들을 코스-장소 DTO 리스트에 저장
            for (CoursePlace coursePlace : course.getCoursePlaces()) {
                CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                    coursePlace.getPlace().getPoi_id(),
                    coursePlace.getSequence_index(),
                    coursePlace.getPlace().getPlace_name(),
                    coursePlace.getPlace().getPlace_category(),
                    coursePlace.getPlace().getPlace_address(),
                    coursePlace.getPlace().getPlace_coordinate_x(),
                    coursePlace.getPlace().getPlace_coordinate_y(),
                    coursePlace.getEnter_time(),
                    coursePlace.getLeave_time()
                    );
                coursePlaces.add(coursePlaceDTO);
            }

            // 코스 DTO에 코스-장소 DTO 리스트 저장
            courseDTO.setCoursePlaces(coursePlaces);
            return courseDTO;
        }
        else {
            return null;
        }

    }

    // 코스 생성
    public CourseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        Course createdCourse = new Course();
        createdCourse.setCourse_name(courseRequestDTO.getCourse_name());
        createdCourse.setCourse_description(courseRequestDTO.getCourse_description());
        createdCourse.setCourse_category(courseRequestDTO.getCourse_category());

        courseRepository.save(createdCourse);

        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_name(createdCourse.getCourse_name());
        courseDTO.setCourse_description(createdCourse.getCourse_description());
        courseDTO.setCourse_category(createdCourse.getCourse_category());
        
        // 코스 DTO 반환
        return courseDTO;
    }

    // 코스 제거
    public void deleteCourseById(Long course_id) {
        courseRepository.deleteById(course_id);
    }
    
}
