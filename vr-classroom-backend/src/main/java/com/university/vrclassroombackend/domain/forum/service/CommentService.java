package com.university.vrclassroombackend.domain.forum.service;

import com.university.vrclassroombackend.domain.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.domain.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.domain.forum.vo.CommentVO;
import com.university.vrclassroombackend.domain.user.vo.UserCommentVO;

import java.util.List;

public interface CommentService {
    List<CommentVO> getPostComments(Integer postId, Integer page);
    Integer createComment(CommentCreateDTO dto, Integer commenterId);
    boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId);
    boolean deleteComment(Integer commentId, Integer commenterId);
    List<UserCommentVO> getUserComments(Integer userId, Integer page);
}
