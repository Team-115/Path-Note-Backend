package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    
    private String course_name;
    private String course_description;
    private Map<String, List<Double>> embeddings; // FastAPI의 벡터가 List<float>라면 List<Double>로 받습니다.
    private double processing_time_ms;
}
