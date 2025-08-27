package com.oneonefive.PathNote.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    // 코스 ID
    private Long course_id;

    // 회원 ID
    private Long user_id;

    // 코스 이름
    private String course_name;

    // 코스 설명
    private String course_description;

    // 코스 카테고리
    private String course_category;

    // 코스 생성 시간
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created_at;

    // 좋아요 갯수
    private Long like_count;
    
    // 코스-장소 리스트
    private List<CoursePlaceDTO> course_places;
}
