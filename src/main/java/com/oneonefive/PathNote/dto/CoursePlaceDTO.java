package com.oneonefive.PathNote.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoursePlaceDTO {

    private Long poi_id;
    private Long sequence_index;
    private String place_name;
    private String place_category;
    private String place_address;
    private Double place_coordinate_x;
    private Double place_coordinate_y;
    private LocalDateTime place_enter_time;
    private LocalDateTime place_leave_time;
    
}
