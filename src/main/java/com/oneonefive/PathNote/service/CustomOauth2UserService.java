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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

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
                    LocalDateTime.now()
            );
        }

        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
    }
    
}
