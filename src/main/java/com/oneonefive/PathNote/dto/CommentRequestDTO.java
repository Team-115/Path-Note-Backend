package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private Long course_id;
    private Long user_id;
    private String content;
}
