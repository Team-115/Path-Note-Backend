package com.oneonefive.PathNote.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

}
