package com.oneonefive.PathNote.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceDTO;
import com.oneonefive.PathNote.dto.CoursePlaceRequestDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * CourseController 단위 테스트
 * @WebMvcTest로 MVC 레이어만 로드하고, Service는 @MockBean으로 대체
 * 각 테스트는 Given(환경/스텁) - When(행위) - Then(검증) 흐름을 주석으로 명확히 분리
 * HTTP Status Code, JSON 필드, 서비스 호출 여부/인자까지 모두 검증
 */
@ActiveProfiles("test")         // application-test.yml 사용
@WebMvcTest(controllers = CourseController.class)
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 보안 필터 비활성화
@DisplayName("코스 컨트롤러 테스트")
public class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;              // HTTP 요청 시뮬레이터
    @Autowired
    ObjectMapper om;              // JSON 직렬화/역직렬화를 위해 사용
    @MockitoBean
    CourseService courseService;  // 실제 빈 대신 가짜 목 주입, 컨트롤러만 검증 위함

    // --------------------------------------------------
    // DTO들을 간결하게 생성하기 위한 헬퍼
    // --------------------------------------------------
    private CoursePlaceDTO placeDto(long seq, String name) {
        // 각 장소의 시간 포맷: "yyyy-MM-dd HH:mm"
        LocalDateTime enter = LocalDateTime.of(2025, 8, 29, 9, 30);
        LocalDateTime leave = LocalDateTime.of(2025, 8, 29, 10, 15);

        CoursePlaceDTO p = new CoursePlaceDTO();
        p.setPoi_id(111111L + seq);
        p.setSequence_index(seq);
        p.setPlace_name(name);
        p.setPlace_category("카페");
        p.setPlace_address("대전광역시 OO구 OO로 123");
        p.setPlace_coordinate_x(127.3845); // 경도
        p.setPlace_coordinate_y(36.3504);  // 위도
        p.setPlace_enter_time(enter);
        p.setPlace_leave_time(leave);
        return p;
    }
    private CourseDTO courseDto(long id, String name, String category) {
        CourseDTO c = new CourseDTO();
        c.setCourse_id(id);
        c.setUser_id(9001L);
        c.setCourse_name(name);
        c.setCourse_description("테스트 코스 설명");
        c.setCourse_category(category);
        c.setCreated_at(LocalDateTime.of(2025, 8, 29, 8, 0));
        c.setLike_count(7L);
        c.setCourse_places(List.of(
                placeDto(1L, "성심당"),
                placeDto(2L, "테스트 카페")
        ));
        return c;
    }
    private CoursePlaceRequestDTO placeReq(long seq, String name) {
        CoursePlaceRequestDTO p = new CoursePlaceRequestDTO();
        p.setPoi_id(222222L + seq);
        p.setSequence_index(seq);
        p.setPlace_name(name);
        p.setPlace_category("빵집");
        p.setPlace_address("대전광역시 XX구 XX로 456");
        p.setPlace_coordinate_x(127.4000);
        p.setPlace_coordinate_y(36.3400);
        p.setPlace_enter_time(LocalDateTime.of(2025, 8, 29, 11, 0));
        p.setPlace_leave_time(LocalDateTime.of(2025, 8, 29, 11, 40));
        return p;
    }
    private CourseRequestDTO courseReq(String name, String category) {
        CourseRequestDTO r = new CourseRequestDTO();
        r.setUser_id(9001L);
        r.setCourse_name(name);
        r.setCourse_description("요청 코스 설명");
        r.setCourse_category(category);
        // NOTE: 원본 DTO는 new ArrayList()로 raw 타입이지만, Jackson 직렬화엔 영향 없음
        r.setCourse_places(List.of(
                placeReq(1, "대흥동 빵집"),
                placeReq(2, "은행동 빵집")
        ));
        return r;
    }


    // GET /api/courses - 코스 전체 조회 (region/category 필터)
    @Nested
    @DisplayName("GET /api/courses - 전체 조회")
    class GetAllCourses {

        @Test
        void 코스_전체_조회_성공() throws Exception {
            // given - 컨트롤러는 region/category 쿼리 파라미터를 service로 전달함
            String region = "대전";
            String category = "맛집";
            List<CourseDTO> data = List.of(
                    courseDto(1L, "대전 맛집 코스 A", "맛집"),
                    courseDto(2L, "대전 맛집 코스 B", "맛집")
            );
            given(courseService.findCourseAll(region, category)).willReturn(data);

            // when - /api/courses?region=대전&category=맛집 GET 요청
            MvcResult result = mockMvc.perform(get("/api/courses")
                            .param("region", region)
                            .param("category", category)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())   // 상태코드는 체인으로 확인
                    .andReturn();                 // 응답 객체를 가져온다

            // then - 배열,필드 검증
            String json = result.getResponse().getContentAsString();        // 응답 JSON 문자열 꺼내기
            CourseDTO[] response = om.readValue(json, CourseDTO[].class);   // JSON → DTO[] 변환
            // assertThat으로 배열/필드 검증
            assertThat(response).hasSize(2);
            assertThat(response[0].getCourse_id()).isEqualTo(1L);
            assertThat(response[0].getUser_id()).isEqualTo(9001L);
            assertThat(response[0].getCourse_name()).isEqualTo("대전 맛집 코스 A");
            assertThat(response[0].getCourse_category()).isEqualTo("맛집");
            assertThat(response[0].getLike_count()).isEqualTo(7);
            assertThat(response[0].getCreated_at().toString()).contains("2025-08-29T08:00");
            assertThat(response[0].getCourse_places()).hasSize(2);
            assertThat(response[0].getCourse_places().get(0).getSequence_index()).isEqualTo(1L);
            assertThat(response[0].getCourse_places().get(0).getPlace_name()).isEqualTo("성심당");

            // 서비스 호출 인자까지 계약 검증
            then(courseService).should().findCourseAll(region, category);
        }

        @Test
        //given 결과가 비어있을 때 when 조회 then 404
        void 코스_전체_조회_빈결과_404() throws Exception {
            // given
            given(courseService.findCourseAll(null, null)).willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isNotFound());

            then(courseService).should().findCourseAll(null, null);
        }
    }

    // GET /api/courses/{course_id} - 단건 조회
    @Nested
    @DisplayName("GET /api/courses/{course_id} - 단건 조회")
    class GetOneCourse {

        @Test
        //given 존재하는 ID when 조회 then 200 + DTO 반환(중첩 포함)
        void 코스_단건_조회_성공() throws Exception {
            // given
            long id = 10L;
            CourseDTO dto = courseDto(id, "부산 여행 코스", "여행");
            given(courseService.findCourseById(id)).willReturn(dto);

            // when
            MvcResult result = mockMvc.perform(get("/api/courses/{course_id}", id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            CourseDTO body = om.readValue(result.getResponse().getContentAsString(), CourseDTO.class);
            assertThat(body.getCourse_id()).isEqualTo(id);
            assertThat(body.getCourse_name()).isEqualTo("부산 여행 코스");
            assertThat(body.getCourse_places()).hasSize(2);
            assertThat(body.getCourse_places().get(1).getPlace_name()).isEqualTo("테스트 카페");
            assertThat(body.getCourse_places().get(1).getPlace_leave_time().toString()).contains("2025-08-29T10:15");

            then(courseService).should().findCourseById(id);
        }

        @Test
        //given 존재하지 않는 ID when 조회 then 404
        void 코스_단건_조회_404() throws Exception {
            // given
            long id = 999L;
            given(courseService.findCourseById(id)).willReturn(null);

            // when & then
            mockMvc.perform(get("/api/courses/{course_id}", id))
                    .andExpect(status().isNotFound());

            then(courseService).should().findCourseById(id);
        }
    }

    // POST /api/courses - 신규 등록
    @Nested
    @DisplayName("POST /api/courses - 신규 등록")
    class CreateCourse {

        @Test
        //given 유효한 요청 when 생성 then 200 + 생성된 DTO 반환
        void 코스_등록_성공() throws Exception {
            // given
            CourseRequestDTO req = courseReq("서울 빵투어", "맛집");
            CourseDTO created = courseDto(101L, req.getCourse_name(), req.getCourse_category());
            given(courseService.createCourse(any(CourseRequestDTO.class))).willReturn(created);

            // when
            MvcResult result = mockMvc.perform(post("/api/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andReturn();

            // then: 응답 검증
            CourseDTO body = om.readValue(result.getResponse().getContentAsString(), CourseDTO.class);
            assertThat(body.getCourse_id()).isEqualTo(101L);
            assertThat(body.getCourse_name()).isEqualTo("서울 빵투어");
            assertThat(body.getCourse_category()).isEqualTo("맛집");
            assertThat(body.getCreated_at().toString()).contains("2025-08-29T08:00");
            assertThat(body.getCourse_places()).hasSize(2);

            // then: 서비스 호출 인자(요청 바디) 캡쳐/검증
            ArgumentCaptor<CourseRequestDTO> captor = ArgumentCaptor.forClass(CourseRequestDTO.class);
            then(courseService).should().createCourse(captor.capture());
            CourseRequestDTO passed = captor.getValue();

            assertThat(passed.getUser_id()).isEqualTo(9001L);
            assertThat(passed.getCourse_name()).isEqualTo("서울 빵투어");
            assertThat(passed.getCourse_category()).isEqualTo("맛집");
            assertThat(passed.getCourse_places()).hasSize(2);
            assertThat(passed.getCourse_places().get(0).getPlace_name()).isEqualTo("대흥동 빵집");
        }

    }
}
