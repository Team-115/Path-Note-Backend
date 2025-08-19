// src/test/java/com/oneonefive/PathNote/controller/CourseControllerTest.java
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given; // BDD 스타일 스텁
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// ↓↓↓ MockMvc용 ResultMatchers 를 사용해야 한다!
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 제외
class CourseControllerTest {

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
}
