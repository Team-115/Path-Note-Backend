package com.oneonefive.PathNote.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceDTO;
import com.oneonefive.PathNote.dto.CoursePlaceRequestDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.dto.HashtagDTO;
import com.oneonefive.PathNote.dto.UserDTO;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.entity.Hashtag;
import com.oneonefive.PathNote.entity.Place;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;
import com.oneonefive.PathNote.repository.CourseRepository;
import com.oneonefive.PathNote.repository.HashtagRepository;
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
    private HashtagRepository hashtagRepository;

    @Autowired
    private EmbeddingService embeddingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            
            // 해쉬태그 DTO 생성
            List<HashtagDTO> hashtagDTOs = new ArrayList<>();
            for (Hashtag hashtag : course.getHashtags()) {
                HashtagDTO hashtagDTO = new HashtagDTO();
                hashtagDTO.setContent(hashtag.getContent());
                hashtagDTOs.add(hashtagDTO);
            }
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
            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                userDTO,
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseCategory(),
                hashtagDTOs,
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
        // 해쉬태그 DTO 생성
        List<HashtagDTO> hashtagDTOs = new ArrayList<>();
        for (Hashtag hashtag : course.getHashtags()) {
            HashtagDTO hashtagDTO = new HashtagDTO();
            hashtagDTO.setContent(hashtag.getContent());
            hashtagDTOs.add(hashtagDTO);
        }
        CourseDTO courseDTO = new CourseDTO(
            course.getCourseId(),
            userDTO,
            course.getCourseName(),
            course.getCourseDescription(),
            course.getCourseCategory(),
            hashtagDTOs,
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
        course.setCourseCategory(courseRequestDTO.getCourse_category());

        // 코스 저장
        course = courseRepository.save(course);

        // 해쉬태그 저장
        List<Hashtag> createdHashtags = new ArrayList<>();
        for (Hashtag hashtag : courseRequestDTO.getCourse_hashtag()) {
            Hashtag createdHashtag = new Hashtag();
            createdHashtag.setCourse(course);
            createdHashtag.setContent(hashtag.getContent());
            createdHashtags.add(createdHashtag);
        }
        hashtagRepository.saveAll(createdHashtags);
        course.setHashtags(createdHashtags);

        // 해쉬태그 DTO 생성
        List<HashtagDTO> hashtagDTOs = new ArrayList<>();
        for (Hashtag hashtag : createdHashtags) {
            HashtagDTO hashtagDTO = new HashtagDTO();
            hashtagDTO.setContent(hashtag.getContent());
            hashtagDTOs.add(hashtagDTO);
        }

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

        // 임베딩 벡터는 null로 저장 (Python 스케줄링으로 나중에 처리)
        course.setEmbeddingVector(null);
        courseRepository.save(course);

        // 코스 DTO 생성
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourse_id(course.getCourseId());
        courseDTO.setUser(userDTO);
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(course.getCourseCategory());
        courseDTO.setCourse_hashtag(hashtagDTOs);
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
        course.getHashtags().clear();

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

        // 해쉬태그 저장
        List<Hashtag> createdHashtags = new ArrayList<>();
        for (Hashtag hashtag : courseRequestDTO.getCourse_hashtag()) {
            Hashtag createdHashtag = new Hashtag();
            createdHashtag.setCourse(course);
            createdHashtag.setContent(hashtag.getContent());
            createdHashtags.add(createdHashtag);
        }
        createdHashtags = hashtagRepository.saveAll(createdHashtags);

        // 해쉬태그 DTO 생성
        List<HashtagDTO> hashtagDTOs = new ArrayList<>();
        for (Hashtag hashtag : createdHashtags) {
            HashtagDTO hashtagDTO = new HashtagDTO();
            hashtagDTO.setContent(hashtag.getContent());
            hashtagDTOs.add(hashtagDTO);
        }

        // 코스 필드 수정
        course.setCourseName(courseRequestDTO.getCourse_name());
        course.setCourseCategory(courseRequestDTO.getCourse_category());
        course.setCourseDescription(courseRequestDTO.getCourse_description());
        course.setCreatedAt(LocalDateTime.now());
        course.getCoursePlaces().addAll(coursePlaces);
        course.getHashtags().addAll(createdHashtags);

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
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(user.getNickname());
        userDTO.setProfilePresetURL(String.format("http://localhost:8080/images/%s.png", user.getProfilePreset()));
        courseDTO.setUser(userDTO);
        courseDTO.setCourse_name(course.getCourseName());
        courseDTO.setCourse_description(course.getCourseDescription());
        courseDTO.setCourse_category(course.getCourseCategory());
        courseDTO.setCourse_hashtag(hashtagDTOs);
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

    // 키워드 검색 (임베딩 기반)
    @Transactional
    public List<CourseDTO> searchCoursesByKeyword(String keyword, int limit) {
        try {
            // 키워드의 임베딩 벡터 생성
            List<Double> keywordEmbedding = embeddingService.getEmbedding(keyword);
            System.out.println(keywordEmbedding);
            System.out.println("sdfsadf");
            if (keywordEmbedding == null) {
                return new ArrayList<>();
            }

            // 모든 코스 조회
            List<Course> allCourses = courseRepository.findAll();
            
            // 유사도 계산 및 정렬
            List<CourseWithSimilarity> coursesWithSimilarity = new ArrayList<>();
            
            for (Course course : allCourses) {
                if (course.getEmbeddingVector() != null) {
                    try {
                        List<Double> courseEmbedding = objectMapper.readValue(
                            course.getEmbeddingVector(), 
                            new TypeReference<List<Double>>() {}
                        );
                        
                        double similarity = calculateCosineSimilarity(keywordEmbedding, courseEmbedding);
                        coursesWithSimilarity.add(new CourseWithSimilarity(course, similarity));
                    } catch (Exception e) {
                        System.err.println("Error parsing embedding for course " + course.getCourseId() + ": " + e.getMessage());
                    }
                }
            }
            
            // 유사도 기준으로 정렬하고 상위 결과만 선택
            List<Course> topCourses = coursesWithSimilarity.stream()
                    .sorted(Comparator.comparingDouble(CourseWithSimilarity::getSimilarity).reversed())
                    .limit(limit)
                    .map(CourseWithSimilarity::getCourse)
                    .collect(Collectors.toList());
            
            // CourseDTO로 변환
            return convertCoursesToDTOs(topCourses);
            
        } catch (Exception e) {
            System.err.println("Error in keyword search: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 코스 임베딩 벡터 업데이트
    @Transactional
    public boolean updateCourseEmbedding(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<Double> embedding = embeddingService.getCourseEmbedding(
                course.getCourseName(), 
                course.getCourseDescription(), 
                course.getCourseCategory()
            );
            
            if (embedding != null) {
                try {
                    String embeddingJson = objectMapper.writeValueAsString(embedding);
                    course.setEmbeddingVector(embeddingJson);
                    courseRepository.save(course);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error saving embedding for course " + courseId + ": " + e.getMessage());
                }
            }
        }
        return false;
    }

    // 임베딩이 null인 코스들 조회 (Python 스케줄링용)
    @Transactional
    public List<CourseDTO> getCoursesWithNullEmbedding() {
        List<Course> courses = courseRepository.findByEmbeddingVectorIsNull();
        return convertCoursesToDTOs(courses);
    }

    // 코스 일괄 임베딩 업데이트 (Python에서 임베딩 값을 전달받아 저장)
    @Transactional
    public boolean updateCourseEmbeddingWithVector(Long courseId, List<Double> embedding) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null && embedding != null) {
            try {
                String embeddingJson = objectMapper.writeValueAsString(embedding);
                course.setEmbeddingVector(embeddingJson);
                courseRepository.save(course);
                return true;
            } catch (Exception e) {
                System.err.println("Error saving embedding vector for course " + courseId + ": " + e.getMessage());
            }
        }
        return false;
    }

    // 코사인 유사도 계산
    private double calculateCosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA.size() != vectorB.size()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Course를 CourseDTO로 변환하는 헬퍼 메서드
    private List<CourseDTO> convertCoursesToDTOs(List<Course> courses) {
        List<CourseDTO> courseDTOs = new ArrayList<>();

        for (Course course : courses) {
            
            // 해쉬태그 DTO 생성
            List<HashtagDTO> hashtagDTOs = new ArrayList<>();
            for (Hashtag hashtag : course.getHashtags()) {
                HashtagDTO hashtagDTO = new HashtagDTO();
                hashtagDTO.setContent(hashtag.getContent());
                hashtagDTOs.add(hashtagDTO);
            }
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
            // (코스 DTO) 생성
            CourseDTO courseDTO = new CourseDTO(
                course.getCourseId(),
                userDTO,
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseCategory(),
                hashtagDTOs,
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

    // 유사도와 함께 코스를 저장하는 내부 클래스
    private static class CourseWithSimilarity {
        private final Course course;
        private final double similarity;

        public CourseWithSimilarity(Course course, double similarity) {
            this.course = course;
            this.similarity = similarity;
        }

        public Course getCourse() {
            return course;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
    
}
