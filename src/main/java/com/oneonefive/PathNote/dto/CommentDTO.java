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
public class CommentDTO {

    // 코스 ID
    private Long course_id;
    
    // 댓글 ID
    private Long comment_id;

    // 댓글 내용
    private String content;

    // 댓글 작성자 정보
    private UserDTO user;

    // 생성 시간
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created_at;
}
