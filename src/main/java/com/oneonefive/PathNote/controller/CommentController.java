package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.dto.CommentRequestDTO;
import com.oneonefive.PathNote.entity.Comment;
import com.oneonefive.PathNote.service.SocialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final SocialService socialService;

    // GET /api/comments/{course_id}
    // course_id에 해당하는 게시물의 댓글 전체 조회
    @GetMapping("/{course_id}/comments")
    public List<CommentDTO> getAllCourses(@PathVariable("course_id") Long course_id) {
        return socialService.getComments(course_id);
    }

    // POST /api/comments/{course_id}
    // course_id에 해당하는 게시물에 댓글 신규 등록
    @PostMapping("/{course_id}/comments")
    public CommentDTO createComment(@PathVariable("course_id") Long course_id, @RequestBody CommentRequestDTO comment) {
        Comment createdComment = socialService.createComment(comment);
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(createdComment.getContent());
        commentDTO.setCourse_id(createdComment.getCourse().getCourseId());
        commentDTO.setUser_id(createdComment.getUser().getUserId());
        return commentDTO;
    }
}
