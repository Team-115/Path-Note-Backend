package com.oneonefive.PathNote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingResponseDTO {
    private String course_name;
    private String course_description;
    private String category;
    private Map<String, List<Double>> embeddings;
    private double processing_time_ms;
}