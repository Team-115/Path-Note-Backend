package com.oneonefive.PathNote.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import org.springframework.stereotype.Service;
import com.oneonefive.PathNote.entity.User;

import com.oneonefive.PathNote.oauth.KakaoOAuth2UserInfo;
import com.oneonefive.PathNote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 사용자 로그인 → 카카오 인증 → 액세스 토큰 획득 → loadUser() 호출 → 사용자 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("Kakao OAuth2 User Attributes: {}", attributes);

        return processKakaoUser(attributes);
    }

    private OAuth2User processKakaoUser(Map<String, Object> attributes) {
        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(attributes);
        Optional<User> optionalUser = userRepository.findByKakaoId(userInfo.getKakaoId());
        
        User user;

        if (optionalUser.isPresent()) {
                // 기존 사용자 업데이트
            user = optionalUser.get().update(userInfo.getNickname());
        } else {
                // 새 사용자 생성
            user = new User(
                    null,
                    userInfo.getKakaoId(),
                    userInfo.getNickname(),
                    LocalDateTime.now(),
                    "A01"
            );
        }

        userRepository.save(user);

        // JWT 토큰 생성을 위해 User 객체를 attributes에 추가 (수정 가능한 새 Map 생성)
        Map<String, Object> modifiableAttributes = new java.util.HashMap<>(attributes);
        modifiableAttributes.put("user", user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                modifiableAttributes,
                "id"
        );
    }
    
}
