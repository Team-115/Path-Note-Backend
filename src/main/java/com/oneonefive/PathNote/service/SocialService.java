package com.oneonefive.PathNote.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.dto.CommentRequestDTO;
import com.oneonefive.PathNote.entity.Comment;
import com.oneonefive.PathNote.entity.Course;
import com.oneonefive.PathNote.entity.Like;
import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.CommentRepository;
import com.oneonefive.PathNote.repository.CourseRepository;
import com.oneonefive.PathNote.repository.LikeRepository;
import com.oneonefive.PathNote.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SocialService {
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private LikeRepository likeRepository;

    // 댓글 전체 조회 (course_id)
    // 코스 단일 조회시 해당 코스에 대한 모든 댓글 조회
    public List<CommentDTO> getComments(Long courseId) {

        // 레포지토리에서 course_id가 조회한 코스의 id값과 동일한 모든 데이터를 조회
        List<Comment> commentList = commentRepository.findByCourse_CourseId(courseId);
        // 댓글 DTO 리스트 생성
        List<CommentDTO> commentDTOList = new ArrayList<>();

        // 필요한 내용들을 댓글 DTO 리스트에 저장
        for (Comment comment : commentList) {
            CommentDTO commentDTO = new CommentDTO(
                    comment.getCommentId(),
                    comment.getCourse().getCourseId(),
                    comment.getUser().getUser_id(),
                    comment.getContent(),
                    comment.getCreatedAt()
                    );
                commentDTOList.add(commentDTO);
        }
        
        // DTO 리스트 날짜순으로 정렬
        commentDTOList.sort(Comparator.comparing(CommentDTO::getCreated_at).reversed());
        // DTO 리스트 반환
        return commentDTOList;
    }

    // 댓글 등록
    public Comment createComment(CommentRequestDTO commentRequestDTO) {
        Course course = courseRepository.findById(commentRequestDTO.getCourse_id()).orElse(null);
        User user = userRepository.findById(commentRequestDTO.getUser_id()).orElse(null);

        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.getContent());
        comment.setCourse(course);
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long comment_id) {
        commentRepository.deleteById(comment_id);
    }
    
    // 좋아요 등록
    @Transactional
    public Like createLike(Like like) {
        Course course = like.getCourse();
        course.setLikeCount(course.getLikeCount() + 1);
        courseRepository.save(course);
        return likeRepository.save(like);
    }

    // 좋아요 삭제
    @Transactional
    public void deleteLike(Long like_id) {
        Like like = likeRepository.findById(like_id).orElse(null);
        if (like != null) {
            Course course = like.getCourse();
            course.setLikeCount(course.getLikeCount() - 1);
            courseRepository.save(course);
            likeRepository.deleteById(like_id);
        }
    }
}
