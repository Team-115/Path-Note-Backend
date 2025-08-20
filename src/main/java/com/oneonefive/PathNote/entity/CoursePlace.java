package com.oneonefive.PathNote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "courseplaces")
public class CoursePlace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courseplace_id")
    private Long courseplace_id;
    // 코스-장소 고유 id

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    // 코스 외래키
    // 코스 테이블에서 1개의 데이터를 바라보기 때문에 ManyToOne

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
    // 장소 외래키
    // 장소 테이블에서 1개의 데이터를 바라보기 때문에 ManyToOne

}
