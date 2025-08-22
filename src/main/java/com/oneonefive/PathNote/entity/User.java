package com.oneonefive.PathNote.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    
    // 회원 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 카카오톡 ID
    @Column(name = "kakao_id")
    private String kakaoId;
  
    // 닉네임
    @Column(name = "nickname")
    private String nickname;
  
    // 계정 생성 시간
    @Column(name = "created_at")
    private LocalDateTime createdAt;
  
    // 프로필 이미지
    @Column(name = "profile_preset")
    private String profilePreset;

    // 닉네임, 프로필 업데이트 메서드
    public User update(String nickname, String profilePreset) {
        this.nickname = nickname;
        this.profilePreset = profilePreset;
        return this;
    }

    // 닉네임 업데이트 메서드
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
