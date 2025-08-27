package com.oneonefive.PathNote.oauth;

import com.oneonefive.PathNote.jwt.JwtUtil;
import com.oneonefive.PathNote.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = (User) oAuth2User.getAttribute("user");
        
        if (user != null) {
            String accessToken = jwtUtil.generateAccessToken(user.getKakaoId(), user.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(user.getKakaoId());
            
            // 프론트엔드로 토큰과 함께 리다이렉트
            String redirectUrl = String.format(
                "http://localhost:5173/?accessToken=%s&refreshToken=%s",
                accessToken, 
                refreshToken
            );
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
            log.info("JWT 토큰 발급 완료 - 사용자: {}", user.getNickname());
        } else {
            // 에러 시에도 리다이렉트
            String errorRedirectUrl = "http://localhost:5173/auth/error?message=사용자 정보를 찾을 수 없습니다.";
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
        }
    }
}