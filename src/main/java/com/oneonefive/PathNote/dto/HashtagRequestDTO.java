package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HashtagRequestDTO {
    
    // 해쉬태그 ID
    private Long hashtag_id;

    // 코스 ID
    private Long course_id;

    // 해쉬태그 내용
    private String content;

}
