package com.oneonefive.PathNote.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.repository.CourseRepository;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> findPlaceAll() {
        return placeRepository.findAll();
    }

    public Place findPlaceById(Long place_id) {
        Optional<Place> place = placeRepository.findById(place_id);
        return place.orElse(null);
    }

    public Place createPlace(Place place) {
        return placeRepository.save(place);
    }

    public void deletePlaceById(Long place_id) {
        placeRepository.deleteById(place_id);
    }

}
