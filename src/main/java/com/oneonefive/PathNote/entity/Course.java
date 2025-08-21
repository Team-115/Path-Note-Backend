package com.oneonefive.PathNote.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
public class Course {
    // 코스 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    // 코스 이름
    private String course_name;
    // 코스 설명
    private String course_description;
    // 코스 카테고리
    private String course_category;

    // 코스 생성 시간
    @CreationTimestamp
    private LocalDateTime created_at;

    // 코스-장소 리스트
    @OneToMany(mappedBy = "course")
    private List<CoursePlace> coursePlaces;

}
