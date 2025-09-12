package com.oneonefive.PathNote.service;

import com.oneonefive.PathNote.dto.EmbeddingRequestDTO;
import com.oneonefive.PathNote.dto.EmbeddingResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final WebClient aiServerWebClient;

    @Autowired
    public EmbeddingService(@Qualifier("aiServerWebClient") WebClient aiServerWebClient) {
        this.aiServerWebClient = aiServerWebClient;
    }

    public List<Double> getCourseEmbedding(String courseName, String courseDescription, String category) {
        try {
            EmbeddingRequestDTO requestDTO = new EmbeddingRequestDTO(courseName, courseDescription, category);
            
            Mono<EmbeddingResponseDTO> responseMono = aiServerWebClient
                    .post()
                    .uri("/embed/course")
                    .bodyValue(requestDTO)
                    .retrieve()
                    .bodyToMono(EmbeddingResponseDTO.class);
            
            EmbeddingResponseDTO response = responseMono.block();
            
            if (response != null && response.getEmbeddings() != null) {
                // combined_embedding을 우선 사용
                Map<String, List<Double>> embeddings = response.getEmbeddings();
                if (embeddings.containsKey("combined_embedding")) {
                    return embeddings.get("combined_embedding");
                }
                // combined_embedding이 없으면 첫 번째 available embedding 사용
                if (!embeddings.isEmpty()) {
                    return embeddings.values().iterator().next();
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error getting course embedding: " + e.getMessage());
            return null;
        }
    }

    // 키워드 검색용 임베딩 (기존 메서드 유지)
    public List<Double> getEmbedding(String text) {
        try {
            // 키워드 검색의 경우 단순 텍스트를 코스명으로 전달
            EmbeddingRequestDTO requestDTO = new EmbeddingRequestDTO(text, "", "");
            
            Mono<EmbeddingResponseDTO> responseMono = aiServerWebClient
                    .post()
                    .uri("/embed/course")
                    .bodyValue(requestDTO)
                    .retrieve()
                    .bodyToMono(EmbeddingResponseDTO.class);
            
            EmbeddingResponseDTO response = responseMono.block();
            
            if (response != null && response.getEmbeddings() != null) {
                // combined_embedding을 우선 사용
                Map<String, List<Double>> embeddings = response.getEmbeddings();
                if (embeddings.containsKey("combined_embedding")) {
                    return embeddings.get("combined_embedding");
                }
                // combined_embedding이 없으면 첫 번째 available embedding 사용
                if (!embeddings.isEmpty()) {
                    return embeddings.values().iterator().next();
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error getting embedding: " + e.getMessage());
            return null;
        }
    }
}