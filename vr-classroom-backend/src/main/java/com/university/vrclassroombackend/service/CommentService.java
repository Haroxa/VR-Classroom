package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.dto.CommentCreateDTO;
import com.university.vrclassroombackend.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.vo.CommentVO;
import com.university.vrclassroombackend.vo.UserCommentVO;

import java.util.List;

public interface CommentService {
    List<CommentVO> getPostComments(Integer postId, Integer page);
    Integer createComment(CommentCreateDTO dto, Integer commenterId);
    boolean updateComment(Integer commentId, CommentUpdateDTO dto, Integer commenterId);
    boolean deleteComment(Integer commentId, Integer commenterId);
    List<UserCommentVO> getUserComments(Integer userId, Integer page);
}
