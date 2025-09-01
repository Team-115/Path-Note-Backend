package com.oneonefive.PathNote.filter;

import com.oneonefive.PathNote.jwt.JwtUtil;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String tokenType = jwtUtil.getTokenType(token);
                
                if ("access".equals(tokenType)) {
                    String kakaoId = jwtUtil.getKakaoIdFromToken(token);
                    
                    Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("인증된 사용자: {}", user.getNickname());
                    }
                } else {
                    log.warn("Access Token이 아닌 토큰으로 API 접근 시도: {}", tokenType);
                }
            } else {
                log.warn("유효하지 않거나 만료된 JWT 토큰");
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/") || 
               path.startsWith("/login/oauth2/") || 
               path.equals("/api/users/refresh") ||
               path.startsWith("/error");
    }
}