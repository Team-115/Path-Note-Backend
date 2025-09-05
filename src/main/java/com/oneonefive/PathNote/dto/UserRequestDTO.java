package com.oneonefive.PathNote.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private Long userId;
    private String nickname;
    private String profilePresetURL;
}
