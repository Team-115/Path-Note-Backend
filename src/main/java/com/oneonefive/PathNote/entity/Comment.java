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
@Table(name = "comments")
public class Comment {

    // 댓글 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 코스 ID
    // 댓글을 등록한 게시물
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 회원 ID
    // 댓글을 등록한 회원
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 내용
    private String content;

    // 생성 시간
    // 댓글을 등록한 시간
    private LocalDateTime createdAt;
}
