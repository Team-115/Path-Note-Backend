package com.oneonefive.PathNote.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "likes")
public class Like {

    // 좋아요 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    // 코스 ID
    // 좋아요를 등록한 게시물
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 회원 ID
    // 좋아요를 등록한 회원
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 생성 시간
    // 좋아요를 누른 시간
    private LocalDateTime createdAt;
}
