package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.LikeRequestDTO;
import com.oneonefive.PathNote.dto.LikeDTO;
import com.oneonefive.PathNote.entity.Like;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.service.SocialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class LikeController {

    @Autowired
    private final SocialService socialService;

    // GET /api/courses/{course_id}/likes
    // 코스 좋아요 갯수 조회
    @GetMapping("/{course_id}/likes")
    public Long getLike(@PathVariable("course_id") Long course_id) {
        return socialService.getLikes(course_id);
    }

    // GET /api/courses/{course_id}/like
    // 코스 좋아요 등록 여부 조회
    @GetMapping("/{course_id}/like")
    public boolean getIsLike(@PathVariable("course_id") Long course_id, @AuthenticationPrincipal User user) {
        Like like = socialService.getLike(course_id, user.getUserId());
        if (like != null) return true;
        else return false;
    }

    // POST /api/courses/{course_id}/likes
    // 코스 좋아요 등록 및 삭제
    @PostMapping("/{course_id}/likes")
    public LikeDTO createLike(@PathVariable("course_id") Long course_id, @AuthenticationPrincipal User user) {
        LikeRequestDTO likeRequestDTO = new LikeRequestDTO();
        likeRequestDTO.setCourse_id(course_id);
        likeRequestDTO.setUser_id(user.getUserId());
        return socialService.clickLike(likeRequestDTO);
    }
}
