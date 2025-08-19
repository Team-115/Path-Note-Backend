package com.oneonefive.PathNote.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.repository.CourseRepository;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findCourseAll()
    {
        return courseRepository.findAll();
    }
}
