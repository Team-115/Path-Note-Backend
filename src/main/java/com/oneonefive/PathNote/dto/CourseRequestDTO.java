package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    private Long course_id;
    private Long user_id;
    private String course_name;
    private String course_description;
    private String course_category;
    private List<CoursePlaceDTO> course_Places;
    private LocalDateTime created_at;
    private Long like_count;
}
