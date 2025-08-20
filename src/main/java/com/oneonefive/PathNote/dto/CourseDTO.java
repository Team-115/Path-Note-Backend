package com.oneonefive.PathNote.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long course_id;
    private String course_name;
    private String course_description;
    private String course_category;
    private LocalDateTime created_at;
    private List<CoursePlaceDTO> coursePlaces;
    
}
