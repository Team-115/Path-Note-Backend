package com.oneonefive.PathNote.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 모든 요청에 대해 인증 없이 접근을 허용합니다.
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            );

        // CSRF 보호를 비활성화합니다. (개발 단계에서 Postman 등으로 API 테스트 시 편리)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}