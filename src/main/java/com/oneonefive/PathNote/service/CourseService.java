package com.oneonefive.PathNote.service;

import java.io.Console;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.repository.CourseRepository;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findCourseAll() {
        return courseRepository.findAll();
    }

    public Course findCourseById(Long course_id) {
        Optional<Course> course = courseRepository.findById(course_id);
        return course.orElse(null);
    }

}
