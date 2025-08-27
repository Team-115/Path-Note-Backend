package com.oneonefive.PathNote.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 프론트 개발 도메인 명시(와일드카드 금지: allowCredentials=true일 때는 "*” 불가)
        cfg.setAllowedOrigins(List.of("http://localhost:5173"));
        // Authorization, Content-Type 포함
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // 실제 사용할 메서드들
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        // 인증정보 포함 허용 (쿠키/Authorization 헤더 등)
        cfg.setAllowCredentials(true);
        // 필요한 경우 노출할 헤더
        cfg.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}