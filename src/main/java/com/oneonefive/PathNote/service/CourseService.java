package com.oneonefive.PathNote.service;

import java.time.LocalDateTime;
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
    public List<CourseDTO> findCourseAll(String region, String category) {

        List<Course> courses;

        // region과 category가 모두 있으면 
        if (region != null && category != null) {
            courses = courseRepository.findByPlaceAddressStartingWithAndCourseCategory(region, category);
        } 
        // region만 있을 경우
        else if (region != null) {
            courses = courseRepository.findByPlaceAddressStartingWith(region);
        }
        // category 있을 경우 (키워드 검색은 코스명이나 설명, 태그 등에서 검색할 수 있습니다)
        else if (category != null) {
            courses = courseRepository.findByCourseCategory(category);
        }
        // region과 category 모두 없을
        else {
            courses = courseRepository.findAll();
        }

        // (코스 DTO 리스트) 생성
        List<CourseDTO> courseDTOs = new ArrayList<>();

        for (Course course : courses) {

            // (코스-장소 DTO 리스트) 생성
            List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();
            // 코스와 연관관계가 맺어져있는 CoursePlace를 CoursePlaceDTO로 변환 후 (코스-장소 DTO 리스트)에 저장
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
                coursePlaceDTOs.add(coursePlaceDTO);
            }

            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                course.getUserId(),
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseCategory(),
                course.getCreatedAt(),
                course.getLikeCount(),
                // (코스-장소 DTO 리스트) 삽입
                coursePlaceDTOs,
                coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_x).average().orElse(0.0),
                coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_y).average().orElse(0.0)
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
            course.getUserId(),
            course.getCourseName(),
            course.getCourseDescription(),
            course.getCourseCategory(),
            course.getCreatedAt(),
            course.getLikeCount(),
            new ArrayList<>(),
            course.getCenterX(),
            course.getCenterY()
        );

        // 코스-장소 DTO 리스트 생성
        List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();

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
            coursePlaceDTOs.add(coursePlaceDTO);
        }

        // 코스 DTO에 코스-장소 DTO 리스트 저장
        courseDTO.setCourse_places(coursePlaceDTOs);
        courseDTO.setCenter_x(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_x).average().orElse(0.0));
        courseDTO.setCenter_y(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_y).average().orElse(0.0));
        return courseDTO;

    }

    // 코스 생성
    @Transactional
    public CourseDTO createCourse(CourseRequestDTO courseRequestDTO) {

        // 장소 레포지토리를 통해 poi_id를 조회하여 장소 데이터가 있는지 확인
        // 없다면 CoursePlaceRequestDTO에 포함된 내용을 토대로 새로운 장소 데이터 생성
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
                coursePlace.getPlace().getPlaceName(),
                coursePlace.getPlace().getPlaceCategory(),
                coursePlace.getPlace().getPlaceAddress(),
                coursePlace.getPlace().getPlaceCoordinateX(),
                coursePlace.getPlace().getPlaceCoordinateY(),
                coursePlace.getEnterTime(),
                coursePlace.getLeaveTime()
                );
            coursePlaceDTOs.add(coursePlaceDTO);
        }

        // 코스 평균 좌표 수정
        course.setCenterX(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_x).average().orElse(0.0));
        course.setCenterY(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_y).average().orElse(0.0));

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(course.getCourseCategory());
        courseDTO.setCreated_at(course.getCreatedAt());
        courseDTO.setLike_count(course.getLikeCount());
        courseDTO.setCourse_places(coursePlaceDTOs);
        courseDTO.setCenter_x(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_x).average().orElse(0.0));
        courseDTO.setCenter_y(coursePlaceDTOs.stream().mapToDouble(CoursePlaceDTO::getPlace_coordinate_y).average().orElse(0.0));
        
        // 코스 DTO 반환
        return courseDTO;
    }

    // 코스 수정
    @Transactional
    public CourseDTO editCourse(Long course_id, CourseRequestDTO courseRequestDTO) {
        
        // 코스 엔티티 조회
        Course course = courseRepository.findById(course_id).orElse(null);

        // 기존 코스-장소 제거
        course.getCoursePlaces().clear();

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

        // 코스 필드 수정
        course.setCourseName(courseRequestDTO.getCourse_name());
        course.setCourseCategory(courseRequestDTO.getCourse_category());
        course.setCourseDescription(courseRequestDTO.getCourse_description());
        course.setCreatedAt(LocalDateTime.now());
        course.getCoursePlaces().addAll(coursePlaces);

        // 코스 저장
        course = courseRepository.save(course);

        // 코스-장소 DTO 리스트 생성
        List<CoursePlaceDTO> coursePlaceDTOs = new ArrayList<>();
        for (CoursePlace coursePlace : coursePlaces) {
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
            coursePlaceDTOs.add(coursePlaceDTO);
        }

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        courseDTO.setUser_id(course.getUserId());
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(course.getCourseCategory());
        courseDTO.setCreated_at(course.getCreatedAt());
        courseDTO.setLike_count(course.getLikeCount());
        courseDTO.setCourse_places(coursePlaceDTOs);

        return courseDTO;
    }

    // 코스 제거
    // 코스-장소 리스트 필드의 Cascade 옵션을 통해 관련 데이터도 함께 제거됨
    @Transactional
    public void deleteCourseById(Long course_id) {
        courseRepository.deleteById(course_id);
    }
    
}
