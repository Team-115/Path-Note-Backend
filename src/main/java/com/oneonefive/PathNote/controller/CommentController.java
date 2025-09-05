package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.dto.CommentRequestDTO;
import com.oneonefive.PathNote.dto.UserRequestDTO;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.service.SocialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final SocialService socialService;

    // GET /api/courses/{course_id}/comments
    // course_id에 해당하는 게시물의 댓글 전체 조회
    @GetMapping("/{course_id}/comments")
    public List<CommentDTO> getAllComments(@PathVariable("course_id") Long course_id) {
        return socialService.getComments(course_id);
    }

    // POST /api/courses/{course_id}/comments
    // course_id에 해당하는 게시물에 댓글 신규 등록
    @PostMapping("/{course_id}/comments")
    public CommentDTO createComment(@PathVariable("course_id") Long course_id, @RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal User user) {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        
        commentRequestDTO.setCourse_id(course_id);
        userRequestDTO.setUserId(user.getUserId());
        commentRequestDTO.setUser(userRequestDTO);

        return socialService.createComment(commentRequestDTO);
    }

    // DELETE /api/courses/{course_id}/comments/{comment_id}
    // course_id에 해당하는 게시물의 comment_id에 해당하는 댓글 삭제
    @DeleteMapping("/{course_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("course_id") Long course_id, @PathVariable("comment_id") Long comment_id, @AuthenticationPrincipal User user) {
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        
        commentRequestDTO.setCourse_id(course_id);
        commentRequestDTO.setComment_id(comment_id);
        userRequestDTO.setUserId(user.getUserId());

        boolean hasDeleted = socialService.deleteComment(commentRequestDTO);
        if (hasDeleted) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
