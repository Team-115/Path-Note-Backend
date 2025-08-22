package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    private Long user_id;
    private Long course_id;
    private String course_name;
    private String course_description;
    private String course_category;
    private LocalDateTime created_at;
}
