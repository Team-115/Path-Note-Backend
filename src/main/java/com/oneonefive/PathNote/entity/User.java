package com.oneonefive.PathNote.entity;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;
    
    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_preset", nullable = false)
    private String profilePreset;

    @Column(nullable = false)
    private LocalDateTime createdAt;

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
