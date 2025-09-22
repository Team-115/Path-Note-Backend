package com.oneonefive.PathNote.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    // 회원 ID
    @Column(name = "user_id")
    private Long userId;

    // 코스 이름
    @Column(name = "course_name")
    private String courseName;
    // 코스 설명
    @Column(name = "course_description")
    private String courseDescription;
    // 코스 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category courseCategory;

    // 코스 생성 시간
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 좋아요 갯수 (트랜잭션으로 관리할 것)
    @Column(name = "like_count")
    private Long likeCount = 0L;

    // 코스-장소 리스트
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoursePlace> coursePlaces;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    // 중심 좌표
    @Column(name = "center_x")
    private Double centerX;
    @Column(name = "center_y")
    private Double centerY;

    // 임베딩 벡터
    @Column(name = "embedding_vector", columnDefinition = "vector")
    private String embeddingVector;
}
