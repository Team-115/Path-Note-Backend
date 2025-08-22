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
    // 코스 전체 조회
    // 코스 페이지 열람시 우측 컴포넌트에 표시
    @GetMapping("/{course_id}/likes")
    public List<CommentDTO> getAllCourses(@PathVariable("course_id") Long course_id) {
        return socialService.getComments(course_id);
    }

    // POST /api/courses/{course_id}/likes
    @PostMapping("/{course_id}/likes")
    public Like createLike() {
        return socialService.createLike(null);
    }
}
