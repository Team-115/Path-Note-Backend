package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.oneonefive.PathNote.entity.Category;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {

    // 회원 ID
    private Long user_id;
    
    // 코스 이름
    private String course_name;

    // 코스 설명
    private String course_description;

    // 코스 카테고리
    private String course_category;

    // 코스 해시태그
    private List<Category> course_hashtag;

    // 코스-장소 리스트
    private List<CoursePlaceRequestDTO> course_places = new ArrayList();

}
