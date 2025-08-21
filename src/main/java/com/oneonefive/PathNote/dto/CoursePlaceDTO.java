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
    // T MAP POI ID
    private Long poi_id;
    // 코스 내에서의 순번
    private Long sequence_index;
    // 장소 이름
    private String place_name;
    // 장소 카테고리
    private String place_category;
    // 장소 주소
    private String place_address;
    // 장소 X좌표
    private Double place_coordinate_x;
    // 장소 Y좌표
    private Double place_coordinate_y;
    // 장소 도착 시간
    private LocalDateTime place_enter_time;
    // 장소 출발 시간
    private LocalDateTime place_leave_time;
}
