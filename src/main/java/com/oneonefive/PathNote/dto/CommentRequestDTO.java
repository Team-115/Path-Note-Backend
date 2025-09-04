package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    
    // 댓글 ID
    private Long comment_id;

    // 코스 ID
    private Long course_id;

    // 회원 ID
    private UserRequestDTO user;

    // 댓글 내용
    private String content;

    // 생성 시간
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created_at;
}
