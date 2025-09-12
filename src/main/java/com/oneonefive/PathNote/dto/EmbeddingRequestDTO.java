package com.oneonefive.PathNote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequestDTO {
    private String course_name;
    private String course_description;
    private String category;
}