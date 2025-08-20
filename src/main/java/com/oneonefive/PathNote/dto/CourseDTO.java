package com.oneonefive.PathNote.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long course_id;
    private String course_name;
    private String course_description;
    private String course_category;
    private List<CoursePlaceDTO> coursePlaces;
    
}
