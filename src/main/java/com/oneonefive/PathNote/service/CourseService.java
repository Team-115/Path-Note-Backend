package com.oneonefive.PathNote.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceDTO;
import com.oneonefive.PathNote.dto.CoursePlaceRequestDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.entity.Place;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;
import com.oneonefive.PathNote.repository.CourseRepository;
import com.oneonefive.PathNote.repository.PlaceRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CoursePlaceRepository coursePlaceRepository;

    // 코스 전체 조회
    @Transactional
    public List<CourseDTO> findCourseAll() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDTO> courseDTOs = new ArrayList<>();

        for (Course course : courses) {

            // (코스-장소 DTO 리스트) 생성
            List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();
            for (CoursePlace coursePlace : course.getCoursePlaces()) {
                CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                    coursePlace.getPlace().getPoiId(),
                    coursePlace.getSequenceIndex(),
                    coursePlace.getEnterTime(),
                    coursePlace.getLeaveTime()
                    );
                coursePlaceDTOs.add(coursePlaceDTO);
            }

            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseCategory(),
                course.getCreatedAt(),
                course.getLikeCount(),
                coursePlaceDTOs
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
        List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();

        // course_id를 가지고있는 코스-장소들을 코스-장소 DTO 리스트에 저장
        for (CoursePlace coursePlace : course.getCoursePlaces()) {
            CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                coursePlace.getPlace().getPoiId(),
                coursePlace.getSequenceIndex(),
                coursePlace.getEnterTime(),
                coursePlace.getLeaveTime()
                );
            coursePlaceDTOs.add(coursePlaceDTO);
        }

        // 코스 DTO에 코스-장소 DTO 리스트 저장
        courseDTO.setCourse_Places(coursePlaceDTOs);
        return courseDTO;

    }

    // 코스 생성
    @Transactional
    public CourseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        for (CoursePlaceRequestDTO coursePlace : courseRequestDTO.getCourse_places()) {
            if (placeRepository.findByPoiId(coursePlace.getPoi_id()) == null) {
                Place newPlace = new Place();
                newPlace.setPoiId(coursePlace.getPoi_id());
                newPlace.setPlaceName(coursePlace.getPlace_name());
                newPlace.setPlaceCategory(coursePlace.getPlace_category());
                newPlace.setPlaceAddress(coursePlace.getPlace_address());
                newPlace.setPlaceCoordinateX(coursePlace.getPlace_coordinate_x());
                newPlace.setPlaceCoordinateY(coursePlace.getPlace_coordinate_y());
                placeRepository.save(newPlace);
            }
        }

        // 코스 데이터 생성
        Course course = new Course();
        course.setUserId(courseRequestDTO.getUser_id());
        course.setCourseName(courseRequestDTO.getCourse_name());
        course.setCourseDescription(courseRequestDTO.getCourse_description());
        course.setCourseCategory(courseRequestDTO.getCourse_category());

        // 코스 저장
        course = courseRepository.save(course);

        // 코스-장소 데이터 생성
        List<CoursePlace> coursePlaces = new ArrayList<>();
        for (CoursePlaceRequestDTO coursePlace : courseRequestDTO.getCourse_places()) {
            CoursePlace createdCoursePlace = new CoursePlace();
            createdCoursePlace.setCourse(course);
            createdCoursePlace.setPlace(placeRepository.findByPoiId(coursePlace.getPoi_id()));
            createdCoursePlace.setSequenceIndex(coursePlace.getSequence_index());
            createdCoursePlace.setEnterTime(coursePlace.getPlace_enter_time());
            createdCoursePlace.setLeaveTime(coursePlace.getPlace_leave_time());
            coursePlaces.add(createdCoursePlace);
        }

        // 코스-장소 저장
        coursePlaces = coursePlaceRepository.saveAll(coursePlaces);
        
        // 코스-장소 DTO 리스트 생성
        List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();
        for (CoursePlace coursePlace : coursePlaces) {
            CoursePlaceDTO coursePlaceDTO = new CoursePlaceDTO (
                coursePlace.getPlace().getPoiId(),
                coursePlace.getSequenceIndex(),
                coursePlace.getEnterTime(),
                coursePlace.getLeaveTime()
                );
            coursePlaceDTOs.add(coursePlaceDTO);
        }

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(course.getCourseCategory());
        courseDTO.setCreated_at(course.getCreatedAt());
        courseDTO.setLike_Count(course.getLikeCount());
        courseDTO.setCourse_Places(coursePlaceDTOs);
        
        // 코스 DTO 반환
        return courseDTO;
    }

    // 코스 제거
    @Transactional
    public void deleteCourseById(Long course_id) {
        courseRepository.deleteById(course_id);
    }
    
}
