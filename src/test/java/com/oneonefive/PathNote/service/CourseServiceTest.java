package com.oneonefive.PathNote.service;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock CourseRepository courseRepository;
    @InjectMocks CourseService courseService;

    @DisplayName("findCourseAll: 레포지토리 결과 그대로 반환")
    @Test
    void findCourseAll_returnsList() {
        // given
        List<Course> stub = List.of(
                new Course(1L, "A", "A설명", "낭만", LocalDateTime.now()),
                new Course(2L, "B", "B설명", "힐링", LocalDateTime.now())
        );
        given(courseRepository.findAll()).willReturn(stub);

        // when
        List<Course> res = courseService.findCourseAll();

        // then
        assertThat(res).hasSize(2);
        verify(courseRepository).findAll();
    }

    @Nested
    @DisplayName("findCourseById")
    class FindById {

        @DisplayName("존재: Optional.present → 엔티티 반환")
        @Test
        void found() {
            // given
            Course c = new Course(10L, "C", "C설명", "사랑", LocalDateTime.now());
            given(courseRepository.findById(10L)).willReturn(Optional.of(c));

            // when
            Course res = courseService.findCourseById(10L);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getCourse_id()).isEqualTo(10L);
            verify(courseRepository).findById(10L);
        }

        @DisplayName("미존재: Optional.empty → null 반환")
        @Test
        void notFound() {
            // given
            given(courseRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Course res = courseService.findCourseById(999L);

            // then
            assertThat(res).isNull();
            verify(courseRepository).findById(999L);
        }
    }

    @DisplayName("createCourse: save 호출 후 저장된 엔티티 반환")
    @Test
    void createCourse_saves() {
        // given
        Course toSave = new Course(null, "새코스", "설명", "힐링", null);
        Course saved  = new Course(100L, "새코스", "설명", "힐링", LocalDateTime.now());
        given(courseRepository.save(any(Course.class))).willReturn(saved);

        // when
        Course res = courseService.createCourse(toSave);

        // then
        assertThat(res.getCourse_id()).isEqualTo(100L);

        // 전달 인자 내용 검증
        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        Course passed = captor.getValue();
        assertThat(passed.getCourse_id()).isNull();
        assertThat(passed.getCourse_name()).isEqualTo("새코스");
        assertThat(passed.getCourse_category()).isEqualTo("힐링");
    }

    @DisplayName("deleteCourseById: 레포지토리의 deleteById 호출")
    @Test
    void deleteCourseById_callsRepository() {
        // when
        courseService.deleteCourseById(7L);

        // then
        verify(courseRepository).deleteById(7L);
    }
}
