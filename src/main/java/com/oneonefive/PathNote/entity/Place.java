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
    private Long poi_id;
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
}
