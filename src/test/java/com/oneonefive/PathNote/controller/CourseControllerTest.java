package com.oneonefive.PathNote.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given; // BDD 스타일 스텁
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 제외
class CourseControllerTest {
    /**
     * MockMvc 내용 추가 필요
     */
    @Autowired MockMvc mockMvc;           // 서버 없이 MVC 요청 시뮬레이션
    @Autowired ObjectMapper objectMapper; // 자바 객체 JSON으로 직렬화/역직렬화

    @MockBean CourseService courseService; // 컨트롤러가 의존하는 서비스 목 주입

    @DisplayName("GET /api/courses - 코스 전체 조회: 200과 리스트 반환")
    @Test
    void getAllCourses() throws Exception {
        // given: 서비스가 반환할 더미 데이터 세팅
        List<Course> testCourseList = List.of(
                new Course(1L, "코스A", "코스A 설명", "낭만 여행", LocalDateTime.now()),
                new Course(2L, "코스B", "코스B 설명", "사랑 여행", LocalDateTime.now())
        );
        // 컨트롤러가 service.findCourseAll()을 호출하면 위의 리스트를 돌려주라고 스텁
        given(courseService.findCourseAll()).willReturn(testCourseList);

        // when: 컨트롤러 엔드포인트로 GET요청
        MvcResult result = mockMvc.perform(get("/api/courses")).andReturn(); // 객체를 직접 받아서 사용
        MockHttpServletResponse res = result.getResponse(); // 응답(상태/헤더/바디) 추출

        // then (AssertJ): 상태/헤더/바디 구조 검증
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());   // 응답 상태코드와 기대하는 상태코드가 200
        assertThat(res.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

        List<Course> body = objectMapper.readValue( // JSON 데이터 역직렬화
                res.getContentAsString(),
                new TypeReference<List<Course>>() {}
        );

        assertThat(body).hasSize(2);    // 리스트 크기 검증
        assertThat(body.get(0).getCourse_name()).isEqualTo("코스A");
        assertThat(body.get(1).getCourse_category()).isEqualTo("사랑 여행");

        // 컨트롤러가 실제로 service.findCourseAll()을 1번 호출했는지 상호작용 검증
        verify(courseService).findCourseAll();

    }

    @DisplayName("POST /api/courses - 코스 생성: 200과 생성된 코스 반환")
    @Test
    void createCourse() throws Exception {
        // given: 요청으로 들어갈 Course 객체와 서비스가 반환할 더미 데이터 준비
        Course requestCourse = new Course(null, "코스C", "코스C 설명", "힐링 여행", LocalDateTime.now());
        Course responseCourse = new Course(3L, "코스C", "코스C 설명", "힐링 여행", requestCourse.getCreated_at());

        // courseService.createCourse 호출 시 responseCourse를 반환하도록 스텁
        given(courseService.createCourse(any(Course.class))).willReturn(responseCourse);

        // when: POST 요청 수행
        MvcResult result = mockMvc.perform(
                post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)          // 요청 타입: JSON
                        .content(objectMapper.writeValueAsString(requestCourse)) // 요청 바디 JSON 직렬화
        ).andReturn();

        MockHttpServletResponse res = result.getResponse();

        // then: 응답 검증
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());   // 상태코드 200
        assertThat(res.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

        Course body = objectMapper.readValue(res.getContentAsString(), Course.class);

        assertThat(body.getCourse_id()).isEqualTo(3L);
        assertThat(body.getCourse_name()).isEqualTo("코스C");
        assertThat(body.getCourse_category()).isEqualTo("힐링 여행");

    }

    @DisplayName("GET /api/courses/{id} - 존재하는 코스: 200과 코스 반환")
    @Test
    void getCourseById_found() throws Exception {
        // given
        Long id = 1L;
        Course course = new Course(
                id,
                "코스A",
                "코스A 설명",
                "낭만 여행",
                LocalDateTime.now()
        );
        given(courseService.findCourseById(id)).willReturn(course);

        // when
        MvcResult result = mockMvc.perform(get("/api/courses/{course_id}", id))
                .andReturn();
        MockHttpServletResponse res = result.getResponse();

        // then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(res.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);

        Course body = objectMapper.readValue(res.getContentAsString(), Course.class);
        assertThat(body.getCourse_id()).isEqualTo(id);
        assertThat(body.getCourse_name()).isEqualTo("코스A");
        assertThat(body.getCourse_category()).isEqualTo("낭만 여행");

        verify(courseService).findCourseById(id);
    }

    @DisplayName("GET /api/courses/{id} - 없는 코스: 404 반환 (바디 없음)")
    @Test
    void getCourseById_notFound() throws Exception {
        // given
        Long id = 999L;
        given(courseService.findCourseById(id)).willReturn(null);

        // when
        MvcResult result = mockMvc.perform(get("/api/courses/{course_id}", id))
                .andReturn();
        MockHttpServletResponse res = result.getResponse();

        // then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(res.getContentAsString()).isEmpty();

        verify(courseService).findCourseById(id);
    }

    @DisplayName("DELETE /api/courses/{id} - 코스 삭제 성공 시 204 반환")
    @Test
    void deleteCourseById_success() throws Exception {
        // given
        Long id = 1L;

        // when
        MvcResult result = mockMvc.perform(delete("/api/courses/{course_id}", id))
                .andReturn();
        MockHttpServletResponse res = result.getResponse();

        // then
        assertThat(res.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(res.getContentAsString()).isEmpty(); // 바디 없음 확인

        // 서비스가 해당 id로 deleteCourseById를 호출했는지 검증
        verify(courseService).deleteCourseById(id);
    }

}
