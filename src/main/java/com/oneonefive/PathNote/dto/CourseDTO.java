package com.oneonefive.PathNote.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.CoursePlace;
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
    private List<CoursePlace> coursePlaces;
    
    @Autowired
    private CoursePlaceRepository coursePlaceRepository;

    public CourseDTO(Course course) {
        this.course_id = course.getCourse_id();
        this.course_name = course.getCourse_name();
        this.course_description = course.getCourse_description();
        this.course_category = course.getCourse_category();
        coursePlaces = new ArrayList<CoursePlace>();
    }
}
