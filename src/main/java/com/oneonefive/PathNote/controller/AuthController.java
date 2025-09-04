package com.oneonefive.PathNote.controller;

import com.oneonefive.PathNote.jwt.JwtUtil;
import com.oneonefive.PathNote.dto.RefreshTokenRequest;
import com.oneonefive.PathNote.dto.TokenResponse;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 토큰이 만료될 경우 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.badRequest().body("유효하지 않은 리프레시 토큰입니다.");
            }
            
            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.badRequest().body("만료된 리프레시 토큰입니다.");
            }
            
            String tokenType = jwtUtil.getTokenType(refreshToken);
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.badRequest().body("리프레시 토큰이 아닙니다.");
            }
            
            String kakaoId = jwtUtil.getKakaoIdFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }
            
            User user = userOptional.get();
            String newAccessToken = jwtUtil.generateAccessToken(user.getKakaoId(), user.getNickname());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getKakaoId());
            
            TokenResponse tokenResponse = new TokenResponse(
                newAccessToken, 
                newRefreshToken, 
                3600 // 1시간 (초 단위)
            );
            
            log.info("토큰 갱신 완료 - 사용자: {}", user.getNickname());
            return ResponseEntity.ok(tokenResponse);
            
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("토큰 갱신에 실패했습니다.");
        }
    }

    // 현재 헤더와 토큰, 사용자 식별
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            if (!authorization.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("잘못된 Authorization 헤더입니다.");
            }
            
            String token = authorization.substring(7);
            
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
            }
            
            String kakaoId = jwtUtil.getKakaoIdFromToken(token);
            Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }
            
            return ResponseEntity.ok(userOptional.get());
            
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("사용자 정보 조회에 실패했습니다.");
        }
    }
}
