package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.entity.Like;
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
    public List<CommentDTO> getLike(@PathVariable("course_id") Long course_id) {
        return socialService.getComments(course_id);
    }

    // POST /api/courses/{course_id}/likes
    // 코스 좋아요 등록
    @PostMapping("/{course_id}/likes")
    public Like createLike() {
        return socialService.createLike(null);
    }
}
