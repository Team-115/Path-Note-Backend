package com.oneonefive.PathNote.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CategoryDTO;
import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceDTO;
import com.oneonefive.PathNote.dto.CoursePlaceRequestDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.dto.HashtagDTO;
import com.oneonefive.PathNote.dto.UserDTO;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.entity.Category;
import com.oneonefive.PathNote.entity.Place;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;
import com.oneonefive.PathNote.repository.CourseRepository;
import com.oneonefive.PathNote.repository.CategoryRepository;
import com.oneonefive.PathNote.repository.PlaceRepository;
import com.oneonefive.PathNote.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CoursePlaceRepository coursePlaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    // 코스 전체 조회
    @Transactional
    public List<CourseDTO> findCourseAll(String region) {

        List<Course> courses;

        // region이 있을 경우
        if (region != null) {
            courses = courseRepository.findByPlaceAddressStartingWith(region);
        }
        // region이 없을 경우
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

            User user = userRepository.findById(course.getUserId()).orElse(null);
            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(user.getNickname());
            userDTO.setProfilePresetURL(String.format("http://localhost:8080/images/%s.png", user.getProfilePreset()));
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategory_id(course.getCourseCategory().getCategoryId());
            categoryDTO.setContent(course.getCourseCategory().getContent());
            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                userDTO,
                course.getCourseName(),
                course.getCourseDescription(),
                categoryDTO,
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
        User user = userRepository.findById(course.getUserId()).orElse(null);
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(user.getNickname());
        userDTO.setProfilePresetURL(String.format("http://localhost:8080/images/%s.png", user.getProfilePreset()));
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategory_id(course.getCourseCategory().getCategoryId());
        categoryDTO.setContent(course.getCourseCategory().getContent());
        CourseDTO courseDTO = new CourseDTO(
            course.getCourseId(),
            userDTO,
            course.getCourseName(),
            course.getCourseDescription(),
            categoryDTO,
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

        User user = userRepository.findById(courseRequestDTO.getUser_id()).orElse(null);
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(user.getNickname());
        userDTO.setProfilePresetURL(String.format("http://localhost:8080/images/%s.png", user.getProfilePreset()));

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
        course.setCourseCategory(categoryService.getAndCreateCategory(courseRequestDTO.getCourse_category()));

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

        // 카테고리 DTO 생성
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategory_id(course.getCourseCategory().getCategoryId());
        categoryDTO.setContent(course.getCourseCategory().getContent());

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        courseDTO.setUser(userDTO);
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(categoryDTO);
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
        User user = userRepository.findById(course.getUserId()).orElse(null);
        
        // 유저 ID 조회
        if (user.getUserId() != courseRequestDTO.getUser_id()) return null;

        // 기존 데이터 제거
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
        course.setCourseCategory(categoryService.getAndCreateCategory(courseRequestDTO.getCourse_category()));
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

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategory_id(course.getCourseCategory().getCategoryId());
        categoryDTO.setContent(course.getCourseCategory().getContent());

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(user.getNickname());
        userDTO.setProfilePresetURL(String.format("http://localhost:8080/images/%s.png", user.getProfilePreset()));
        courseDTO.setUser(userDTO);
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(categoryDTO);
        courseDTO.setCreated_at(course.getCreatedAt());
        courseDTO.setLike_count(course.getLikeCount());
        courseDTO.setCourse_places(coursePlaceDTOs);
        courseDTO.setCenter_x(course.getCenterX());
        courseDTO.setCenter_y(course.getCenterY());

        return courseDTO;
    }

    // 코스 제거
    // 코스-장소 리스트 필드의 Cascade 옵션을 통해 관련 데이터도 함께 제거됨
    @Transactional
    public boolean deleteCourseById(Long course_id, Long user_id) {
        Course course = courseRepository.findById(course_id).orElse(null);
        if (course != null && course.getUserId() == user_id) {
            courseRepository.deleteById(course_id);
            return true;
        }
        else {
            return false;
        }
    }
    
}
