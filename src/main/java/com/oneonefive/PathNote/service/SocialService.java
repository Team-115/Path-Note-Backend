package com.oneonefive.PathNote.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CommentDTO;
import com.oneonefive.PathNote.entity.Comment;
import com.oneonefive.PathNote.entity.Like;
import com.oneonefive.PathNote.repository.CommentRepository;
import com.oneonefive.PathNote.repository.LikeRepository;

@Service
public class SocialService {
    
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
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long comment_id) {
        commentRepository.deleteById(comment_id);
    }

    // 좋아요 등록
    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    // 좋아요 삭제
    public void deleteLike(Long like_id) {
        likeRepository.deleteById(like_id);
    }
}
