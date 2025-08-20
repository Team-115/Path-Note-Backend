package com.oneonefive.PathNote.oauth;

import java.util.Map;

public class KakaoOAuth2UserInfo {
    // 카카오 API에서 반환하는 json 데이터를 저장하는 맵
    private Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getKakaoId() {
        return String.valueOf(attributes.get("id"));
    }

    public String getNickname() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) {
            return null;
        }
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        if (profile == null) {
            return null;
        }
        return (String) profile.get("nickname");
    }
}