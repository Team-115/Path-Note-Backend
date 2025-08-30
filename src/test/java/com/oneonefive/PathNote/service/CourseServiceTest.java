package com.oneonefive.PathNote.service;

import com.oneonefive.PathNote.dto.CourseDTO;
import com.oneonefive.PathNote.dto.CoursePlaceRequestDTO;
import com.oneonefive.PathNote.dto.CourseRequestDTO;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.entity.Place;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;
import com.oneonefive.PathNote.repository.CourseRepository;
import com.oneonefive.PathNote.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("코스 서비스 테스트")
class CourseServiceTest {

    @InjectMocks CourseService courseService;
    @Mock CourseRepository courseRepository;
    @Mock PlaceRepository placeRepository;
    @Mock CoursePlaceRepository coursePlaceRepository;

    // ---------- 픽스처 헬퍼 ----------
    private Place place(long poiId, String name) {
        Place p = new Place();
        p.setPoiId(poiId);
        p.setPlaceName(name);
        p.setPlaceCategory("카페");
        p.setPlaceAddress("대전");
        p.setPlaceCoordinateX(127.0);
        p.setPlaceCoordinateY(36.0);
        return p;
    }
    private CoursePlace cp(Course c, Place p, long seq, int sh, int sm, int lh, int lm) {
        CoursePlace cp = new CoursePlace();
        cp.setCourse(c);
        cp.setPlace(p);
        cp.setSequenceIndex(seq);
        cp.setEnterTime(LocalDateTime.of(2025, 8, 29, sh, sm));
        cp.setLeaveTime(LocalDateTime.of(2025, 8, 29, lh, lm));
        return cp;
    }
    private Course course(long id, long userId, String name, String category, List<CoursePlace> cps) {
        Course c = new Course();
        c.setCourseId(id);
        c.setUserId(userId);
        c.setCourseName(name);
        c.setCourseDescription("desc");
        c.setCourseCategory(category);
        c.setCreatedAt(LocalDateTime.of(2025, 8, 29, 8, 0));
        c.setLikeCount(7L);
        // ★ 컬렉션 NPE 방지
        c.setCoursePlaces(new ArrayList<>());
        if (cps != null) c.getCoursePlaces().addAll(cps);
        return c;
    }

    // ---------- 테스트 ----------
    @Nested
    @DisplayName("findCourseAll")
    class FindCourseAll {

        @Test
        void region_and_category_필터로_조회_성공() {
            var p = place(111001L, "성심당");
            var c1 = course(1L, 9001L, "대전 코스 A", "맛집",
                    List.of(cp(null, p, 1L, 9,30, 10,15)));
            var c2 = course(2L, 9001L, "대전 코스 B", "맛집",
                    List.of(cp(null, p, 1L, 9,30, 10,15)));

            given(courseRepository.findByPlaceAddressStartingWithAndCourseCategory("대전","맛집"))
                    .willReturn(List.of(c1, c2));

            List<CourseDTO> result = courseService.findCourseAll("대전","맛집");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCourse_name()).isEqualTo("대전 코스 A");
            assertThat(result.get(0).getCourse_places()).hasSize(1);
        }

        @Test
        void 필터_없이_전체_조회_성공() {
            var p = place(111001L, "성심당");
            var c = course(10L, 9001L, "전국 코스", "여행",
                    List.of(cp(null, p, 1L, 9,30, 10,15)));
            given(courseRepository.findAll()).willReturn(List.of(c));

            List<CourseDTO> result = courseService.findCourseAll(null,null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCourse_id()).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("findCourseById")
    class FindCourseById {

        @Test
        void 존재하는_코스_조회_성공() {
            var p = place(111001L, "성심당");
            var c = course(10L, 9001L, "부산 코스", "여행",
                    List.of(cp(null, p, 2L, 9,30, 10,15)));

            given(courseRepository.findById(10L)).willReturn(Optional.of(c));

            CourseDTO dto = courseService.findCourseById(10L);

            assertThat(dto.getCourse_id()).isEqualTo(10L);
            assertThat(dto.getCourse_places()).hasSize(1);
            assertThat(dto.getCourse_places().get(0).getPlace_name()).isEqualTo("성심당");
        }
    }

    @Nested
    @DisplayName("createCourse")
    class CreateCourse {

        @Test
        void 코스_생성_성공() {
            // 요청 DTO
            var r1 = new CoursePlaceRequestDTO();
            r1.setPoi_id(222001L);
            r1.setSequence_index(1L);
            r1.setPlace_name("대흥동 빵집");
            r1.setPlace_category("빵집");
            r1.setPlace_address("대전");
            r1.setPlace_coordinate_x(127.1); r1.setPlace_coordinate_y(36.3);
            r1.setPlace_enter_time(LocalDateTime.of(2025,8,29,11,0));
            r1.setPlace_leave_time(LocalDateTime.of(2025,8,29,11,40));

            var r2 = new CoursePlaceRequestDTO();
            r2.setPoi_id(222002L);
            r2.setSequence_index(2L);
            r2.setPlace_name("은행동 빵집");
            r2.setPlace_category("빵집");
            r2.setPlace_address("대전");
            r2.setPlace_coordinate_x(127.2); r2.setPlace_coordinate_y(36.31);
            r2.setPlace_enter_time(LocalDateTime.of(2025,8,29,12,0));
            r2.setPlace_leave_time(LocalDateTime.of(2025,8,29,12,40));

            var req = new CourseRequestDTO();
            req.setUser_id(9001L);
            req.setCourse_name("서울 빵투어");
            req.setCourse_description("desc");
            req.setCourse_category("맛집");
            req.setCourse_places(List.of(r1, r2));

            // 순차 스텁: 첫 호출 null(없음) → 두 번째 호출 Place(생김)
            given(placeRepository.findByPoiId(222001L))
                    .willReturn(null, place(222001L, "대흥동 빵집"));
            given(placeRepository.findByPoiId(222002L))
                    .willReturn(null, place(222002L, "은행동 빵집"));

            // save(Place) → 그대로 반환
            willAnswer(inv -> inv.getArgument(0)).given(placeRepository).save(any(Place.class));

            // save(Course) → id/createdAt 세팅
            willAnswer(inv -> {
                Course c = inv.getArgument(0);
                c.setCourseId(101L);
                c.setCreatedAt(LocalDateTime.of(2025,8,29,8,0));
                c.setLikeCount(7L);
                return c;
            }).given(courseRepository).save(any(Course.class));

            // saveAll 그대로 반환
            willAnswer(inv -> inv.getArgument(0))
                    .given(coursePlaceRepository).saveAll(anyList());

            // when
            CourseDTO dto = courseService.createCourse(req);

            // then
            assertThat(dto.getCourse_id()).isEqualTo(101L);
            assertThat(dto.getCourse_name()).isEqualTo("서울 빵투어");
            assertThat(dto.getCourse_places()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("editCourse")
    class EditCourse {

        @Test
        void 코스_전체_치환_방식_수정_성공() {
            // 기존 코스
            var existing = course(55L, 9001L, "OLD", "OLD_CAT", List.of());
            given(courseRepository.findById(55L)).willReturn(Optional.of(existing));

            // 요청
            var r = new CoursePlaceRequestDTO();
            r.setPoi_id(901L);
            r.setSequence_index(1L);
            r.setPlace_enter_time(LocalDateTime.of(2025,8,29,9,0));
            r.setPlace_leave_time(LocalDateTime.of(2025,8,29,10,0));

            var req = new CourseRequestDTO();
            req.setCourse_name("제주 올레 코스");
            req.setCourse_category("트레킹");
            req.setCourse_description("새 desc");
            req.setCourse_places(List.of(r));

            given(placeRepository.findByPoiId(901L)).willReturn(place(901L, "제주 포인트"));
            willAnswer(inv -> inv.getArgument(0)).given(courseRepository).save(any(Course.class));

            CourseDTO dto = courseService.editCourse(55L, req);

            assertThat(dto.getCourse_id()).isEqualTo(55L);
            assertThat(dto.getCourse_name()).isEqualTo("제주 올레 코스");
            assertThat(dto.getCourse_places()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("deleteCourseById")
    class DeleteCourseById {
        @Test
        void 삭제_성공() {
            courseService.deleteCourseById(77L);
            then(courseRepository).should().deleteById(77L);
        }
    }
}
