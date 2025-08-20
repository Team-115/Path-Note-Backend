package com.oneonefive.PathNote.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.entity.CoursePlace;
import com.oneonefive.PathNote.repository.CoursePlaceRepository;

@Service
public class CoursePlaceService {

    @Autowired
    private CoursePlaceRepository coursePlaceRepository;

    public List<CoursePlace> findCoursePlaceAll() {
        return coursePlaceRepository.findAll();
    }

    public CoursePlace findCoursePlaceById(Long coursePlace_id) {
        Optional<CoursePlace> coursePlace = coursePlaceRepository.findById(coursePlace_id);
        return coursePlace.orElse(null);
    }

    public CoursePlace createCoursePlace(CoursePlace coursePlace) {
        return coursePlaceRepository.save(coursePlace);
    }

    public void deleteCoursePlaceById(Long coursePlace_id) {
        coursePlaceRepository.deleteById(coursePlace_id);
    }

}
