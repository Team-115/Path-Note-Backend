package com.oneonefive.PathNote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "places")
public class Place {
    // 장소 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long place_id;
    // T MAP POI ID
    @Column(name = "poi_id")
    private Long poiId;
    // 장소 이름
    @Column(name = "place_name")
    private String placeName;
    // 장소 카테고리
    @Column(name = "place_category")
    private String placeCategory;
    // 장소 주소
    @Column(name = "place_address")
    private String placeAddress;
    // 장소 X좌표
    @Column(name = "place_coordinate_x")
    private Double placeCoordinateX;
    // 장소 Y좌표
    @Column(name = "place_coordinate_y")
    private Double placeCoordinateY;
}
