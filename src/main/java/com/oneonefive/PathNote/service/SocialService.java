package com.oneonefive.PathNote.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.dto.CommentRequestDTO;
import com.oneonefive.PathNote.dto.LikeDTO;
import com.oneonefive.PathNote.dto.LikeRequestDTO;
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
    @Transactional
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
                    comment.getUser().getUserId(),
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
    @Transactional
    public CommentDTO createComment(CommentRequestDTO commentRequestDTO) {
        Course course = courseRepository.findById(commentRequestDTO.getCourse_id()).orElse(null);
        User user = userRepository.findById(commentRequestDTO.getUser_id()).orElse(null);

        Comment comment = new Comment();
        comment.setCourse(course);
        comment.setUser(user);
        comment.setContent(commentRequestDTO.getContent());
        comment = commentRepository.save(comment);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setComment_id(comment.getCommentId());
        commentDTO.setCourse_id(comment.getCourse().getCourseId());
        commentDTO.setUser_id(comment.getUser().getUserId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreated_at(comment.getCreatedAt());
        return commentDTO;
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteComment(CommentRequestDTO commentRequestDTO) {
        Comment comment = commentRepository.findById(commentRequestDTO.getComment_id()).orElse(null);
        if (comment == null) return false;
        
        if (comment.getUser().getUserId() == commentRequestDTO.getUser_id()) {
            commentRepository.deleteById(commentRequestDTO.getComment_id());
            return true;
        }
        else {
            return false;
        }
    }
    
    // 좋아요 등록
    @Transactional
    public LikeDTO clickLike(LikeRequestDTO likeRequestDTO) {
        Course course = courseRepository.findById(likeRequestDTO.getCourse_id()).orElse(null);
        User user = userRepository.findById(likeRequestDTO.getUser_id()).orElse(null);

        Like existingLike = likeRepository.findByCourse_CourseIdAndUser_UserId(likeRequestDTO.getCourse_id(), likeRequestDTO.getUser_id());
        // 이미 좋아요를 눌렀을 경우
        if (existingLike != null) {
            course.setLikeCount(course.getLikeCount() - 1);
            course = courseRepository.save(course);
            likeRepository.deleteById(existingLike.getLikeId());

            return null;
        }
        // 좋아요를 처음 눌렀을 경우
        else {
            course.setLikeCount(course.getLikeCount() + 1);
            course = courseRepository.save(course);
        
            Like like = new Like();
            like.setCourse(course);
            like.setUser(user);
            like = likeRepository.save(like);

            LikeDTO likeDTO = new LikeDTO();
            likeDTO.setLike_id(like.getLikeId());
            likeDTO.setCourse_id(like.getLikeId());
            likeDTO.setUser_id(like.getUser().getUserId());
            likeDTO.setCreated_at(like.getCreatedAt());
            return likeDTO;
        }
    }

    // 좋아요 삭제
    @Transactional
    public void deleteLike(Long likeId) {
        Like like = likeRepository.findById(likeId).orElse(null);
        if (like != null) {
            Course course = like.getCourse();
            course.setLikeCount(course.getLikeCount() - 1);
            courseRepository.save(course);
            likeRepository.deleteById(likeId);
        }
    }

    // 좋아요 조회
    @Transactional
    public Like getLike(Long courseId, Long userId) {
        return likeRepository.findByCourse_CourseIdAndUser_UserId(courseId, userId);
    }

    // 좋아요 갯수 조회
    @Transactional
    public Long getLikes(Long courseId) {
        List<Like> likes = likeRepository.findByCourse_CourseId(courseId);
        return Long.valueOf(likes.size());
    }
}
